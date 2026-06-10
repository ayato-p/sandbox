# 05: E2E specファイル — 基本シナリオ（01〜10）

## 実装すること

`e2e/demo-api-e2e/specs/` に基本的な E2E テストシナリオ 10 本を作成する。各 spec は固有のリージョン/日付を使い、並列実行時にスタブが競合しないようにする。

### spec ファイル一覧

1. **`01_health_check.spec`** — ヘルスチェック
   - `GET /health` が 200 と `{ "status": "ok" }` を返す

2. **`02_weather_tokyo_sunny.spec`** — 東京・晴れ
   - スタブ: tokyo, 2026-06-01 → 晴れレスポンス
   - demo-api の加工済みレスポンスを検証

3. **`03_weather_osaka_rainy.spec`** — 大阪・雨
   - スタブ: osaka, 2026-06-02 → 雨レスポンス
   - レスポンス構造とフィールド値を検証

4. **`04_weather_nagoya_cloudy.spec`** — 名古屋・曇り
   - スタブ: nagoya, 2026-06-03 → 曇りレスポンス

5. **`05_weather_sapporo_snow.spec`** — 札幌・雪
   - スタブ: sapporo, 2026-06-04 → 雪レスポンス

6. **`06_weather_invalid_region.spec`** — 不正なリージョン
   - スタブ: invalid-region → 400エラーレスポンス
   - demo-api がエラーを適切に返すことを検証

7. **`07_weather_missing_params.spec`** — パラメータ不足
   - region なし、date なし、両方なしの3パターン
   - 400 エラーを検証

8. **`08_weather_invalid_date.spec`** — 不正な日付形式
   - スタブ: tokyo, invalid-date → エラー
   - 不正な日付形式（`2026/06/01`, `abc` 等）を検証

9. **`09_weather_api_error.spec`** — 天気予報APIの500エラー
   - スタブ: niigata, 2026-06-09 → 500レスポンス
   - demo-api がエラーハンドリングして適切なレスポンスを返すことを検証

10. **`10_weather_api_timeout.spec`** — 天気予報APIタイムアウト
    - スタブ: okinawa, 2026-06-10 → 大幅な遅延レスポンス
    - demo-api がタイムアウトエラーを返すことを検証

### 必要に応じて追加するステップ実装

- 既存ステップで足りない場合は `WeatherSteps.java` にステップを追加する
- 新しいスタブパターンが必要な場合は `WireMockHelper.java` を拡張する

## DONE

- [ ] `e2e/demo-api-e2e/specs/` に 10 個の spec ファイルが存在する
- [ ] 各 spec ファイルに少なくとも 1 つのシナリオがある
- [ ] spec 07 は 3 つ以上のシナリオを持つ（パラメータ不足パターン）
- [ ] すべてのステップに対応する Java ステップ実装が存在する
- [ ] ヘルスチェック、各地域の天気、エラーケース、タイムアウトがカバーされている
- [ ] 各 spec が固有のリージョン/日付を使用し、並列実行時に競合しない
