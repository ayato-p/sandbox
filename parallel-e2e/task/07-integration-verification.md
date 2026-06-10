# 07: 統合検証

## 実装すること

全体の統合動作を確認し、READMEとして使い方をまとめる。

### README

プロジェクトルートに `README.md` を作成:
- プロジェクト概要
- 前提条件（Node.js, pnpm, Java 21, Maven, Docker, kubectl, Skaffold, Helm, Gauge）
- demo-api のローカルビルド手順
- Skaffold でローカル k8s にデプロイする手順
- E2E テスト実行手順（単体・並列）
- 環境変数設定一覧

### 並列実行設定

- `e2e/demo-api-e2e/env/default/default.properties` に並列ストリーム数を設定
- 並列実行コマンド例: `mvn gauge:execute -DspecsDir=specs -DinParallel=true -Dnodes=4`

### 統合動作確認

以下を順に確認し、問題があれば修正する:
1. `pnpm install && pnpm --filter demo-api build` が成功する
2. `docker build` が成功する
3. `helm template` が有効なマニフェストを出力する
4. `skaffold render` が成功する
5. `mvn compile -f e2e/demo-api-e2e/pom.xml` が成功する

## DONE

- [ ] プロジェクトルートに `README.md` が存在し、ビルド・デプロイ・テスト手順が記載されている
- [ ] `pnpm install` と `pnpm --filter demo-api build` が成功する
- [ ] `docker build` が成功する（Docker が利用可能な場合）
- [ ] `helm template` が有効な Kubernetes マニフェストを出力する
- [ ] `skaffold render` が成功する（Skaffold が利用可能な場合）
- [ ] `mvn compile -f e2e/demo-api-e2e/pom.xml` が成功する
- [ ] 並列実行設定がドキュメントに記載されている
