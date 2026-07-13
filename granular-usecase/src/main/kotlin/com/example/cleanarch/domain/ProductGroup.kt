package com.example.cleanarch.domain

/**
 * ドメイン: **商品グループ**（ProductGroup）。
 *
 * 商品グループは 2 つの専用ポート由来のデータから導出される:
 *  - ヘッダ情報 [ProductGroupHead] … グループそのものの情報（専用ポートその1）
 *  - 明細情報 [Product] … グループに属する商品（専用ポートその2）
 *
 * ドメインはポート（インターフェース）を一切知らない。
 * ユースケースが Gateway のメソッドを **関数** として渡し、この [assembleAll] で組み立てる。
 */
data class ProductGroup(
    val id: ProductGroupId,
    val name: String,
    val products: List<Product>,
) {
    companion object {
        /**
         * 2 つの取得関数から商品グループの一覧を組み立てる。
         *
         * @param listHeads グループヘッダを全件取得する関数（専用ポートその1由来）
         * @param listProducts グループ ID から所属商品を取得する関数（専用ポートその2由来）
         */
        fun assembleAll(
            listHeads: () -> List<ProductGroupHead>,
            listProducts: (ProductGroupId) -> List<Product>,
        ): List<ProductGroup> =
            listHeads().map { head ->
                ProductGroup(id = head.id, name = head.name, products = listProducts(head.id))
            }
    }
}

@JvmInline
value class ProductGroupId(val value: String)

/** 専用ポートその1が返すグループのヘッダ情報。 */
data class ProductGroupHead(val id: ProductGroupId, val name: String)

/** 専用ポートその2が返す商品。[basePrice] は基準価格。 */
data class Product(val id: String, val name: String, val basePrice: Int)
