# MicroProfile Examples

[gemini_notebook_microprofile_brief.md](../gemini_notebook_microprofile_brief.md) のスライドで示す MicroProfile API を、同じ要件で **Quarkus** と **Helidon MP** の2実装で示すサンプルです。

## 共通機能

| 問題領域 | MicroProfile 仕様 | 実装 |
|---------|------------------|------|
| 設定値を外から渡す | Config | `@ConfigProperty` |
| 外部サービスは失敗する | Fault Tolerance | `@Retry`, `@Timeout` |
| 生きているか知りたい | Health | `@Liveness` チェック |
| 他サービスを呼びたい | REST Client | `@RegisterRestClient` |
| API仕様を公開したい | OpenAPI | `@Operation` 等 |

## プロジェクト

- [`quarkus-payment-demo/`](quarkus-payment-demo/) — Quarkus 3.x + Kotlin
- [`helidon-payment-demo/`](helidon-payment-demo/) — Helidon MP 4.x + Kotlin

## 起動

```bash
# Quarkus (port 8080)
cd quarkus-payment-demo && ./mvnw quarkus:dev

# Helidon (port 8081)
cd helidon-payment-demo && mvn package && java -jar target/helidon-payment-demo.jar
```

## 動作確認

```bash
# 決済 API
curl -s -X POST http://localhost:8080/api/payments \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"order-1","amount":1000}'

# Health (Quarkus)
curl -s http://localhost:8080/q/health/live

# OpenAPI (Quarkus)
curl -s http://localhost:8080/q/openapi

# Helidon (port 8081)
curl -s -X POST http://localhost:8081/api/payments \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"order-1","amount":1000}'
curl -s http://localhost:8081/health/live
curl -s http://localhost:8081/openapi
```

`payment.gateway.simulate-failures=2` を設定すると、組み込みゲートウェイが最初の2回 503 を返し、`@Retry` による再試行を確認できます。
