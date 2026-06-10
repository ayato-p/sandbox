FROM node:22-slim AS builder

RUN corepack enable && corepack prepare pnpm@10 --activate

WORKDIR /app

COPY apps/demo-api/package.json apps/demo-api/pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

COPY apps/demo-api/tsconfig.json ./
COPY apps/demo-api/src ./src
RUN pnpm build

FROM node:22-slim AS runner

RUN corepack enable && corepack prepare pnpm@10 --activate

WORKDIR /app

COPY --from=builder /app/package.json /app/pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile --prod

COPY --from=builder /app/dist ./dist

EXPOSE 3000

CMD ["node", "dist/index.js"]
