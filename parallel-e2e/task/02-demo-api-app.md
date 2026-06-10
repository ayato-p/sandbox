# 02: demo-api Honoアプリケーション

## 実装すること

`apps/demo-api/` に Hono フレームワークを使ったシンプルなAPIサーバーを実装する。

### 作成ファイル

- `apps/demo-api/package.json` — 依存関係: `hono`, `@hono/node-server`、dev依存: `typescript`, `tsx`, `@types/node`
- `apps/demo-api/tsconfig.json` — TypeScript 設定
- `apps/demo-api/src/index.ts` — メインアプリケーション

### エンドポイント

#### `GET /health`
- `{ "status": "ok" }` を返す

#### `GET /weather?region=<region>&date=<YYYY-MM-DD>`
- 環境変数 `WEATHER_API_URL` で設定された天気予報APIに対してリクエストを送信
- 天気予報APIからのJSONレスポンスを加工して返却
  - 加工例: リクエストタイムスタンプの付与、必要フィールドの抽出・整形
- パラメータバリデーション: `region` と `date` が必須、不足時は 400 エラー
- 天気予報APIのエラーレスポンス（4xx, 5xx）をハンドリングして適切なエラーを返す
- タイムアウト設定: 環境変数 `WEATHER_API_TIMEOUT_MS`（デフォルト 120000ms）

### 設定

- ポート: 環境変数 `PORT`（デフォルト 3000）
- 天気予報API URL: 環境変数 `WEATHER_API_URL`
- タイムアウト: 環境変数 `WEATHER_API_TIMEOUT_MS`

## DONE

- [ ] `apps/demo-api/package.json` が存在し、`hono`, `@hono/node-server` が依存に含まれる
- [ ] `apps/demo-api/tsconfig.json` が存在する
- [ ] `apps/demo-api/src/index.ts` が Hono サーバーを実装している
- [ ] `GET /health` が 200 と `{ "status": "ok" }` を返す
- [ ] `GET /weather` がパラメータ不足時に 400 を返す
- [ ] `GET /weather?region=tokyo&date=2026-01-01` が天気予報APIを呼び出して加工済みJSONを返す
- [ ] 天気予報APIのエラー時に適切なHTTPステータスを返す
- [ ] `pnpm install` がワークスペースパッケージを解決する
- [ ] `pnpm --filter demo-api dev` でサーバーが起動する
- [ ] `pnpm --filter demo-api build` で TypeScript コンパイルが成功する
