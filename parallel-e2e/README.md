# parallel-e2e

WireMockを使ったE2Eテストの並列実行時の問題を検証するデモプロジェクト。Hono APIサーバー + WireMock + Gauge E2Eテスト。

## 前提条件

- Node.js 22+
- pnpm 10+
- Java 21
- Maven 3.6+
- Docker
- kubectl
- Helm 3
- Skaffold
- Gauge (+ java plugin)
- mirrord（mirrord経由でのテスト実行時）

## プロジェクト構成

```
apps/demo-api/          # Hono APIサーバー
environments/demo-api/  # Docker/Helm/Skaffold設定
e2e/demo-api-e2e/       # Gauge E2Eテスト
```

## demo-api ローカルビルド

```bash
cd apps/demo-api
pnpm install
pnpm build
```

### ローカル起動

```bash
cd apps/demo-api
WEATHER_API_URL=http://localhost:8080 pnpm dev
```

### 環境変数

| 変数 | デフォルト | 説明 |
|------|-----------|------|
| `PORT` | `3000` | サーバーポート |
| `WEATHER_API_URL` | - | 天気予報APIのベースURL |
| `WEATHER_API_TIMEOUT_MS` | `120000` | 天気予報APIタイムアウト (ms) |

## K8sデプロイ（Skaffold）

```bash
cd environments/demo-api
skaffold dev
```

demo-api（ポート3000）とWireMock（ポート8080）がローカルにポートフォワードされます。

## E2Eテスト

### コンパイル

```bash
mvn test-compile -f e2e/demo-api-e2e/pom.xml
```

### 実行（逐次）

```bash
cd e2e/demo-api-e2e
mvn gauge:execute -DspecsDir=specs
```

逐次実行では各specが順番にWireMockスタブを登録・検証・削除するため、全テストがパスします。

### 実行（並列）

```bash
cd e2e/demo-api-e2e
mvn gauge:execute -DspecsDir=specs -DinParallel=true -Dnodes=4
```

**並列実行では一部のテストが失敗します。** これは意図的な設計です。複数のspecファイルが同じ `(region, date)` の組み合わせ（tokyo/2026-06-01、osaka/2026-06-02、nagoya/2026-06-03 の3パターン）でWireMockスタブを登録しますが、期待するweatherレスポンスが異なるため、並列実行時にスタブが上書きされて衝突します。

### E2E接続設定

`e2e/demo-api-e2e/env/default/default.properties`:

```properties
demo_api_base_url = http://localhost:3000
wiremock_base_url = http://localhost:8080
```

シェルの環境変数で上書きできます:

```bash
demo_api_base_url=http://demo-api:80 wiremock_base_url=http://wiremock:8080 mvn gauge:execute -DspecsDir=specs
```

> **注意:** Mavenの `-D` オプション（システムプロパティ）ではGaugeのrunnerプロセスに値が伝搬しません。必ずシェルの環境変数として渡してください。

## mirrordを使ったE2Eテスト実行

mirrordを使うと、ポートフォワードなしでローカルからk8sクラスタ内のサービスに直接アクセスしてテストを実行できます。

### 逐次実行

```bash
cd e2e/demo-api-e2e
demo_api_base_url=http://demo-api:80 wiremock_base_url=http://wiremock:8080 \
  mirrord exec --target deployment/demo-api -n default -- \
  mvn gauge:execute -DspecsDir=specs
```

### 並列実行

```bash
cd e2e/demo-api-e2e
demo_api_base_url=http://demo-api:80 wiremock_base_url=http://wiremock:8080 \
  mirrord exec --target deployment/demo-api -n default -- \
  mvn gauge:execute -DspecsDir=specs -DinParallel=true -Dnodes=4
```

### mirrord利用時の注意点

- 接続先URLは環境変数で上書きする必要があります（`demo_api_base_url`, `wiremock_base_url`）。k8sのService名（`demo-api:80`, `wiremock:8080`）を指定します
- `--target deployment/demo-api` でdemo-apiのPodをターゲットにします。これによりmirrordがクラスタネットワークへのアクセスを提供します
- Java標準の `java.net.http.HttpClient`（NIO）でも問題なく動作します

## WireMockスタブの衝突について

各specファイルは以下の3パターンの `(region, date)` のみを使用しています:

| region | date |
|--------|------|
| tokyo | 2026-06-01 |
| osaka | 2026-06-02 |
| nagoya | 2026-06-03 |

逐次実行時は `@BeforeSpec` / `@AfterSpec` フックによりスタブが適切に管理されますが、並列実行時は複数のspecが同じキーに対して異なるレスポンスを登録するため、WireMockの「最後に登録されたスタブが優先」という動作により衝突が発生します。

例:
- spec 02: `(tokyo, 2026-06-01)` → sunny を期待
- spec 05: `(tokyo, 2026-06-01)` → snow を期待
- spec 16: `(tokyo, 2026-06-01)` → rainy を期待

これらが同時に実行されると、最後にスタブを登録したspecのレスポンスが返され、他のspecのアサーションが失敗します。
