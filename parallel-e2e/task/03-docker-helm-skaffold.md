# 03: コンテナ化とK8sデプロイ設定

## 実装すること

demo-api と WireMock を Kubernetes にデプロイするための Docker/Helm/Skaffold 設定を作成する。

### demo-api Dockerfile

`environments/demo-api/app/Dockerfile`:
- マルチステージビルド
  - ステージ1: pnpm で依存インストール + ビルド
  - ステージ2: プロダクションイメージ（コンパイル済み出力 + 本番依存のみ）
- ポート 3000 を公開
- pnpm ワークスペース構造を考慮（ルート `package.json`, `pnpm-workspace.yaml`, `pnpm-lock.yaml`, `apps/demo-api/` をコピー）

### demo-api Helm チャート

`environments/demo-api/app/helm/`:
- `Chart.yaml` — チャート名 `demo-api`
- `values.yaml` — 設定可能な値:
  - `image.repository`, `image.tag`
  - `service.port`（デフォルト 80）
  - `containerPort`（デフォルト 3000）
  - `env.WEATHER_API_URL`
  - `replicaCount`（デフォルト 1）
- `templates/deployment.yaml` — `/health` へのヘルスチェックプローブ付き
- `templates/service.yaml` — ClusterIP Service

### WireMock デプロイ

`environments/demo-api/wiremock/`:
- WireMock の Helm チャートまたは k8s マニフェスト
- ポート 8080 で起動

### Skaffold

`environments/demo-api/skaffold.yaml`:
- demo-api のビルド + デプロイ
- WireMock のデプロイ
- ポートフォワーディング設定（ローカル開発用）

## DONE

- [ ] `environments/demo-api/app/Dockerfile` が存在し、プロジェクトルートから `docker build` が成功する
- [ ] `environments/demo-api/app/helm/Chart.yaml` が存在する
- [ ] `environments/demo-api/app/helm/values.yaml` が設定可能な値を含む
- [ ] `environments/demo-api/app/helm/templates/deployment.yaml` がヘルスチェックプローブを含む
- [ ] `environments/demo-api/app/helm/templates/service.yaml` が存在する
- [ ] WireMock のデプロイ設定が存在する
- [ ] `environments/demo-api/skaffold.yaml` が存在する
- [ ] `helm template environments/demo-api/app/helm` が有効な YAML をレンダリングする
