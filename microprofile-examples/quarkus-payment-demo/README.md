# Quarkus Payment Demo

MicroProfile API を Quarkus で実装したサンプル。

## 起動

```bash
./mvnw quarkus:dev
```

## エンドポイント

| パス | 説明 |
|------|------|
| `POST /api/payments` | 決済処理（`@Retry`, `@Timeout`, REST Client） |
| `GET /q/health/live` | Liveness チェック |
| `GET /q/openapi` | OpenAPI 定義 |

## 主要コード

- `PaymentService` — `@ConfigProperty`, `@Retry`, `@Timeout`
- `PaymentClient` — `@RegisterRestClient`
- `PaymentGatewayHealthCheck` — `@Liveness`

## Fault Tolerance の確認

`application.properties` で `payment.gateway.simulate-failures=2` にすると、組み込みゲートウェイ `/gateway/pay` が最初の2リクエストで 503 を返し、`@Retry` による再試行を確認できます。
