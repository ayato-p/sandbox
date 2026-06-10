# 04: E2Eテスト基盤（Gauge + Java + Maven）

## 実装すること

`e2e/demo-api-e2e/` に Gauge + Java + Maven の E2E テストプロジェクトを構築する。

### Maven プロジェクト

`e2e/demo-api-e2e/pom.xml`:
- Java 21
- 依存関係:
  - `com.thoughtworks.gauge:gauge-java` — Gauge ランナー
  - `org.wiremock:wiremock-standalone` — WireMock クライアント（Admin API 操作用）
  - `java.net.http.HttpClient`（標準ライブラリ使用）
  - `com.fasterxml.jackson.core:jackson-databind` — JSON処理
  - `org.assertj:assertj-core` — アサーション
- プラグイン: `maven-compiler-plugin`, `gauge-maven-plugin`

### Gauge マニフェスト

`e2e/demo-api-e2e/manifest.json`:
- Language: `java`
- Plugins: `html-report`

### 設定ファイル

`e2e/demo-api-e2e/env/default/default.properties`:
- `demo_api_base_url = http://localhost:3000`
- `wiremock_base_url = http://localhost:8080`

### ユーティリティクラス

- `src/test/java/com/example/e2e/utils/HttpHelper.java`
  - demo-api への HTTP リクエスト送信
  - レスポンスのステータスコード・ボディ取得
- `src/test/java/com/example/e2e/utils/WireMockHelper.java`
  - WireMock Admin API を使ったスタブ登録（`POST /__admin/mappings`）
  - スタブ個別削除（`DELETE /__admin/mappings/{id}`）— 並列実行時に他 spec のスタブを壊さないため
  - レスポンス遅延設定対応
  - 各種レスポンスシナリオ（成功、エラー、タイムアウト）

### フック

- `src/test/java/com/example/e2e/hooks/SpecHooks.java`
  - `@BeforeSpec`: スタブ登録（各 spec で使うスタブID一覧を管理）
  - `@AfterSpec`: 登録したスタブIDのみ削除（全リセットしない）

### 共通ステップ

- `src/test/java/com/example/e2e/steps/WeatherSteps.java`
  - 天気予報APIスタブ登録ステップ（成功レスポンス、エラーレスポンス、遅延付き等）
  - demo-api への GET リクエスト送信ステップ
  - レスポンスステータスコード検証ステップ
  - レスポンスボディフィールド検証ステップ
  - レスポンスボディ全体の構造検証ステップ

### 並列実行時のスタブ分離戦略

- 各 spec は固有のリージョン/日付の組み合わせでスタブを登録する
- WireMock のスタブ登録時に返される ID を記録し、AfterSpec でそのIDのみ削除する
- 全リセット（`POST /__admin/reset`）は使わない

## DONE

- [ ] `e2e/demo-api-e2e/pom.xml` が存在し、必要な依存関係がすべて含まれている
- [ ] `e2e/demo-api-e2e/manifest.json` が存在し、`language: java` が設定されている
- [ ] `e2e/demo-api-e2e/env/default/default.properties` に接続情報が設定されている
- [ ] `mvn compile -f e2e/demo-api-e2e/pom.xml` が成功する
- [ ] `WireMockHelper` がスタブ登録と個別削除をサポートしている
- [ ] `HttpHelper` が GET リクエスト送信とレスポンス解析をサポートしている
- [ ] `@BeforeSpec` / `@AfterSpec` フックが実装されている
- [ ] 共通ステップがスタブ登録、リクエスト送信、レスポンス検証をカバーしている
