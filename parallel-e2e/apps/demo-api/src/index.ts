import { Hono } from "hono";
import { serve } from "@hono/node-server";

const app = new Hono();

const WEATHER_API_URL = process.env.WEATHER_API_URL;
const WEATHER_API_TIMEOUT_MS = Number(
  process.env.WEATHER_API_TIMEOUT_MS ?? "5000"
);
const PORT = Number(process.env.PORT ?? "3000");

app.get("/health", (c) => {
  return c.json({ status: "ok" });
});

app.get("/weather", async (c) => {
  const region = c.req.query("region");
  const date = c.req.query("date");

  if (!region) {
    return c.json({ error: "Missing required parameter: region" }, 400);
  }
  if (!date) {
    return c.json({ error: "Missing required parameter: date" }, 400);
  }

  if (!WEATHER_API_URL) {
    return c.json({ error: "WEATHER_API_URL is not configured" }, 500);
  }

  const url = `${WEATHER_API_URL}/weather?region=${encodeURIComponent(region)}&date=${encodeURIComponent(date)}`;

  try {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), WEATHER_API_TIMEOUT_MS);

    const response = await fetch(url, { signal: controller.signal });
    clearTimeout(timeout);

    if (!response.ok) {
      const body = await response.text();
      try {
        return c.json(JSON.parse(body), response.status as any);
      } catch {
        return c.json(
          { error: `Weather API error: ${response.status}` },
          response.status as any
        );
      }
    }

    const data = await response.json();
    return c.json({
      ...data,
      requestTimestamp: new Date().toISOString(),
    });
  } catch (e: unknown) {
    if (e instanceof Error && e.name === "AbortError") {
      return c.json({ error: "Weather API timeout" }, 504);
    }
    return c.json({ error: "Failed to fetch weather data" }, 502);
  }
});

serve({ fetch: app.fetch, port: PORT }, (info) => {
  console.log(`Server is running on port ${info.port}`);
});

export default app;
