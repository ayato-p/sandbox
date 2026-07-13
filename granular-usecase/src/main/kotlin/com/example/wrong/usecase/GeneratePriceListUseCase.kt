package com.example.wrong.usecase

import com.example.wrong.domain.PriceEntry
import com.example.wrong.domain.ProductGroup
import com.example.wrong.domain.SalesChannel
import com.example.wrong.usecase.port.*

/**
 * ユースケース: 商品グループと販売チャネルのあらゆる組み合わせから価格表（価格エントリの集合）を
 * 生成し、逐次保存する。
 *
 * このレイヤの責務:
 *  1. Gateway（ポート実装）の **メソッドを関数として** ドメインの組み立て関数に渡し、
 *     商品グループ／販売チャネルを導出する。
 *  2. 商品グループ × その商品 × 販売チャネル × その地域 の **4 重ループ** で価格エントリを生成する。
 *  3. 生成した価格エントリを 1 件ずつ [PriceEntryPort.save] に渡して逐次保存する。
 */
class GeneratePriceListUseCase(
    private val productGroupHeadPort: ProductGroupHeadPort,
    private val productPort: ProductPort,
    private val salesChannelHeadPort: SalesChannelHeadPort,
    private val regionPort: RegionPort,
    private val priceEntryPort: PriceEntryPort,
) {
    fun execute(): Int {
        // Gateway のメソッドを関数参照として渡し、ドメイン側で商品グループ／販売チャネルを組み立てる。
        val groups: List<ProductGroup> =
            ProductGroup.assembleAll(productGroupHeadPort::findAll, productPort::findByGroup)
        val channels: List<SalesChannel> =
            SalesChannel.assembleAll(salesChannelHeadPort::findAll, regionPort::findByChannel)

        var savedCount = 0
        // 4 重ループ: 商品グループ → 商品 → 販売チャネル → 対象地域
        for (group in groups) {
            for (product in group.products) {
                for (channel in channels) {
                    for (region in channel.regions) {
                        val priceEntry = PriceEntry.combine(group, product, channel, region)
                        priceEntryPort.save(priceEntry) // 逐次保存
                        savedCount++
                    }
                }
            }
        }
        return savedCount
    }
}
