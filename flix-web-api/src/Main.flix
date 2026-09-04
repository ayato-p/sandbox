import java.nio.charset.StandardCharsets
import com.sun.net.httpserver.{HttpServer, HttpHandler, HttpExchange}
import java.net.InetSocketAddress
import java.util.concurrent.CountDownLatch

def main(): Unit \ IO =
    let address = new InetSocketAddress("localhost", 3000);
    let server = HttpServer.create(address, 0);

    let pingHandler = new HttpHandler {
        def handle(_this: HttpHandler, exchange: HttpExchange): Unit \ IO =
            if (exchange.getRequestMethod() == "GET")
                let bytes = "pong".getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/plain; charset=utf-8"
                );

                exchange.sendResponseHeaders(200, 4i64);

                let output = exchange.getResponseBody();
                output.write(bytes);
                output.close()
            else
                exchange.sendResponseHeaders(405, -1i64);
                exchange.close()
    };

    server.createContext("/v1/systems/ping", pingHandler);
    server.setExecutor(null);
    server.start();

    let shutdownLatch = new CountDownLatch(1);
    shutdownLatch.await()
			
