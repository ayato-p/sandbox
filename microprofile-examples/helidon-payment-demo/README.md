# Helidon MP Payment Demo

MicroProfile API を Helidon MP で実装したサンプル。Quarkus 版と同じ API・同じ `org.eclipse.microprofile.*` アノテーションを使用しています。

## 起動

```bash
mvn package
java -jar target/helidon-payment-demo.jar
```

デフォルトポート: **8081**

## エンドポイント

| パス | 説明 |
|------|------|
| `POST /api/payments` | 決済処理（`@Retry`, `@Timeout`, REST Client） |
| `GET /health/live` | Liveness チェック |
| `GET /openapi` | OpenAPI 定義 |

## 主要コード

- `PaymentService` — `@ConfigProperty`, `@Retry`, `@Timeout`
- `PaymentClient` — `@RegisterRestClient`
- `PaymentGatewayHealthCheck` — `@Liveness`

## Fault Tolerance の確認

`META-INF/microprofile-config.properties` で `payment.gateway.simulate-failures=2` にすると、組み込みゲートウェイ `/gateway/pay` が最初の2リクエストで 503 を返し、`@Retry` による再試行を確認できます。
