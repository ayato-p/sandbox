# Mirrordの検証用アプリケーションの開発

最終的にMirrordの検証をしたいので、そのためにかんたんなアプリケーションを作って欲しい。
最低限ローカルのk8sにデプロイしてE2Eから接続できる程度のレベルでok。

## 全体構成

- environments/demo-api/skaffold.yaml -> k8sにデプロイしたり、開発時に使える
- environments/demo-api/app/Dockerfile ｰ> demo-apiをビルドするためのDockerfile
- environments/demo-api/app/helm -> k8sにデプロイするためのHelmファイル（今回Deployment, Serviceくらいのかんたんなもの）
- apps/demo-api -> Honoで作ったかんたんなAPI
- e2e/demo-api-e2e -> Gaugeで実行できるE2Eテスト

## demo-apiの詳細

- pnpmで構成管理されている
- Honoフレームワークで実装されている
- 天気予報APIに依存している
  - 天気予報APIからJSONレスポンスを受け取って多少加工して返却する

## 天気予報APIの詳細（実装対象ではない）

- 地域と日付を渡すと日本国内の天気を返してくれる
- 海外や不正な地域を渡すとエラーレスポンスを返す
- だいたい20~60s程度レスポンスが返ってくるのに時間がかかる

## E2E

- GaugeとJavaで書くこと
- Mavenで構成管理すること
- demo-apiに対してHTTPリクエストを送信して、HTTPレスポンスを検証する
- 今回テストを並列実行する検証をしたいので、specファイルが20個くらい用意する（多少重複した内容でも構わないが極力いろんなパターンを検証しようとする気持ちがあるとよい）
- 天気予報APIとdemo-apiはskaffoldなどで立ち上げているので、接続先情報を設定で変更できるとよい
- 天気予報APIはWireMockとして用意されるので、E2Eがspecファイルごとにスタブを登録する
