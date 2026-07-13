package com.example.practice.domain

/**
 * ドメイン: **価格エントリ**（PriceEntry）。
 *
 * 価格エントリは **商品グループと販売チャネルの組み合わせ** から作られる派生ドメイン。
 * 具体的には (商品グループ, その商品, 販売チャネル, その対象地域) の 1 通りごとに
 * 1 つの価格エントリが生まれる。
 */
data class PriceEntry(
    val code: String,
    val productGroupId: ProductGroupId,
    val productId: String,
    val salesChannelId: SalesChannelId,
    val regionId: String,
    val amount: Int,
) {
    companion object {
        /**
         * 商品グループ／販売チャネルとそれぞれの明細を 1 つずつ受け取り、価格エントリを組み立てる。
         *
         * 価格は「商品の基準価格 × 地域の価格係数」で決まる。
         */
        fun combine(
            group: ProductGroup,
            product: Product,
            channel: SalesChannel,
            region: Region,
        ): PriceEntry =
            PriceEntry(
                code = "${group.id.value}:${product.id}-${channel.id.value}:${region.id}",
                productGroupId = group.id,
                productId = product.id,
                salesChannelId = channel.id,
                regionId = region.id,
                amount = product.basePrice * region.rate,
            )
    }
}
