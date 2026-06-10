# 01: pnpmモノレポのセットアップ

## 実装すること

プロジェクトルートに pnpm ワークスペースとしてのモノレポ構成を作成する。

### 作成ファイル

- `pnpm-workspace.yaml` — ワークスペースパッケージ定義（`apps/*`）
- `package.json` — ルート package.json（`"private": true`、プロジェクト名）
- `.gitignore` — `node_modules/`, `dist/`, `.env` 等

### 要件

- pnpm のワークスペース機能を使い、`apps/` 以下のパッケージを管理する
- ルート `package.json` は `private: true` とする
- `.gitignore` は Node.js プロジェクトとして一般的なエントリを含む

## DONE

- [ ] `pnpm-workspace.yaml` が存在し、`apps/*` が記載されている
- [ ] ルート `package.json` が存在し、`"private": true` が設定されている
- [ ] `.gitignore` が存在し、`node_modules/`, `dist/` が含まれている
- [ ] プロジェクトルートで `pnpm install` が成功する
