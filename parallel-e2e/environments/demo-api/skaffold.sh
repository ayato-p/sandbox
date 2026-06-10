function skaffold() {
  # 'parallel deploy' サブコマンドが呼ばれた場合の処理
  if [[ "$1" == "parallel" && "$2" == "deploy" ]]; then
    shift 2
    local prefix=""
    local num=1

    # オプション引数のパース
    while [[ $# -gt 0 ]]; do
      case $1 in
        --prefix)
          prefix="$2"
          shift 2
          ;;
        -n)
          num="$2"
          shift 2
          ;;
        *)
          echo "Unknown argument: $1"
          return 1
          ;;
      esac
    done

    if [[ -z "$prefix" ]]; then
      echo "Error: --prefix is required. (e.g., --prefix 'some-ns-')"
      return 1
    fi

    # 1. ビルド＆ローカルK8sへのインポートフェーズ
    echo "🚀 Building images and loading into local Kubernetes cluster..."
    local build_file=$(mktemp)

    # command を使うことで元のskaffoldコマンドを呼び出す（無限ループ防止）
    command skaffold build --file-output="$build_file" -q

    if [ $? -ne 0 ]; then
      echo "❌ Build failed."
      rm -f "$build_file"
      return 1
    fi

    # 2. 並列デプロイフェーズ
    echo "🚀 Deploying to $num namespaces in parallel..."
    for i in $(seq 1 $num); do
      local ns="${prefix}${i}"

      # 名前空間が存在しない場合は作成（並列処理の前に行う）
      kubectl create namespace "$ns" --dry-run=client -o yaml | kubectl apply -f - > /dev/null

      echo "⏳ Starting deploy for namespace: $ns"

      # バックグラウンドでSkaffoldデプロイを実行し、ビルド成果物を使い回す
      command skaffold deploy --build-artifacts="$build_file" -n "$ns" &
    done

    # すべてのバックグラウンドプロセスの完了を待つ
    wait

    echo "✅ All parallel deployments finished!"
    rm -f "$build_file"

  else
    # parallel deploy 以外のコマンドはそのまま本来の skaffold に渡す
    command skaffold "$@"
  fi
}