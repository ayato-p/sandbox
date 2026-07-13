# granular-usecase — クリーンアーキテクチャ デモ (Kotlin)

ユースケースを中心に、**ポート**（ドメインを受け渡す境界）と**ドメイン**のみを実装した最小デモ。
アダプタ／インフラ層（DB, Web など）は含みません。

## 題材

価格表の生成。4 重ループが自然に発生するよう次のように設定しています。

| ドメイン | 意味 | 導出元 |
|---|---|---|
| `ProductGroup` | 商品グループ（明細に `Product`「商品」を持つ） | 専用ポート 2 つ（ヘッダ用 / 商品用） |
| `SalesChannel` | 販売チャネル（明細に `Region`「対象地域」を持つ） | 専用ポート 2 つ（ヘッダ用 / 地域用） |
| `PriceEntry` | 価格エントリ | `商品グループ × 商品 × 販売チャネル × 地域` の組み合わせ |

## レイヤ構成

```
pom.xml                        # Maven ビルド定義（Kotlin + JUnit5）
src/main/kotlin/com/example/cleanarch/
├── domain/                       # ドメイン（ポートを知らない）
│   ├── ProductGroup.kt           #   ProductGroup / Product と assembleAll(関数2つ受け取り)
│   ├── SalesChannel.kt           #   SalesChannel / Region と assembleAll(関数2つ受け取り)
│   └── PriceEntry.kt             #   PriceEntry.combine(グループ, 商品, チャネル, 地域)
└── usecase/
    ├── port/                     # ポート（境界インターフェース）
    │   ├── ProductGroupPorts.kt  #   ProductGroupHeadPort / ProductPort
    │   ├── SalesChannelPorts.kt  #   SalesChannelHeadPort / RegionPort
    │   └── PriceEntryPort.kt     #   PriceEntryPort(save)
    └── GeneratePriceListUseCase.kt  # ユースケース（4重ループ + 逐次保存）
```

## ポイント

- **ドメインはポートを知らない**。ユースケースが Gateway（ポート実装）のメソッドを
  `productGroupHeadPort::findAll` のように **関数参照** として `ProductGroup.assembleAll(...)`
  に渡し、ドメイン側で組み立てる。
- ユースケースは 商品グループ・その商品・販売チャネル・その地域 の **4 重ループ** で
  `PriceEntry` を生成し、`PriceEntryPort.save` に 1 件ずつ渡して **逐次保存** する。

## 実行

Java / Maven は SDKMAN で導入します。

```bash
sdk install java 21.0.11-tem
sdk install maven
mvn test
```
