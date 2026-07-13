package com.example.practice.domain

/**
 * ドメイン: **販売チャネル**（SalesChannel）。
 *
 * 販売チャネルは 2 つの専用ポート由来のデータから導出される:
 *  - ヘッダ情報 [SalesChannelHead] … チャネルそのものの情報（専用ポートその1）
 *  - 明細情報 [Region] … チャネルが扱う対象地域（専用ポートその2）
 *
 * 商品グループと同様、ドメインはポートを知らず、ユースケースから渡された **関数** で組み立てる。
 */
data class SalesChannel(
    val id: SalesChannelId,
    val name: String,
    val regions: List<Region>,
) {
    companion object {
        /**
         * 2 つの取得関数から販売チャネルの一覧を組み立てる。
         *
         * @param listHeads チャネルヘッダを全件取得する関数（専用ポートその1由来）
         * @param listRegions チャネル ID から対象地域を取得する関数（専用ポートその2由来）
         */
        context(
            listHeads: () -> List<SalesChannelHead>,
            listRegions: (SalesChannelId) -> List<Region>,
        )
        fun assembleAll(): List<SalesChannel> =
            listHeads().map { head ->
                SalesChannel(id = head.id, name = head.name, regions = listRegions(head.id))
            }
    }
}

@JvmInline
value class SalesChannelId(val value: String)

/** 専用ポートその1が返すチャネルのヘッダ情報。 */
data class SalesChannelHead(val id: SalesChannelId, val name: String)

/** 専用ポートその2が返す対象地域。[rate] は価格係数。 */
data class Region(val id: String, val name: String, val rate: Int)
