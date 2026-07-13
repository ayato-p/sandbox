package com.example.practice.usecase

import com.example.practice.domain.*
import com.example.practice.usecase.port.PriceEntryPort

/**
 * ユースケース: 商品グループと販売チャネルのあらゆる組み合わせから価格表（価格エントリの集合）を
 * 生成し、逐次保存する。
 *
 * このレイヤの責務:
 *  1. Gateway（ポート実装）の **メソッドを関数として** ドメインの組み立て関数に渡し、
 *     商品グループ／販売チャネルを導出する。
 *  2. 商品グループ × その商品 × 販売チャネル × その地域 の **4 重ループ** で価格エントリを生成する。
 *  3. 生成した価格エントリを 1 件ずつ [PriceEntryPort.save] に渡して逐次保存する。
 * @return 保存した価格エントリの件数
 */
context(
    listProductGroupHeads: () -> List<ProductGroupHead>,
    listProducts: (ProductGroupId) -> List<Product>,
    listSalesChannelHeads: () -> List<SalesChannelHead>,
    listRegions: (SalesChannelId) -> List<Region>,
    save: (PriceEntry) -> Unit,
)
fun executeGeneratePriceList(): Int {
    val groups: List<Pair<ProductGroup, Product>> =
        ProductGroup.assembleAll()
            .flatMap { group -> group.products.map { product -> group to product } }
    val channels: List<Pair<SalesChannel, Region>> =
        SalesChannel.assembleAll()
            .flatMap { channel -> channel.regions.map { region -> channel to region } }

    return groups.flatMap { (group, product) ->
        channels.map { (channel, region) -> PriceEntry.combine(group, product, channel, region) }
    }
        .onEach(save)
        .count()
}
