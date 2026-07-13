package com.example.wrong.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DomainTest {

    @Test
    fun `ProductGroup_assembleAll は渡された 2 つの関数からドメインを組み立てる`() {
        val heads = listOf(
            ProductGroupHead(ProductGroupId("G1"), "グループ1"),
            ProductGroupHead(ProductGroupId("G2"), "グループ2"),
        )
        val productsByGroup = mapOf(
            ProductGroupId("G1") to listOf(Product("a", "商品A", 100)),
            ProductGroupId("G2") to listOf(Product("b", "商品B", 200), Product("c", "商品C", 300)),
        )

        // ポートのメソッドの代わりにラムダを関数として渡す
        val groups = ProductGroup.assembleAll(
            listHeads = { heads },
            listProducts = { id -> productsByGroup.getValue(id) },
        )

        assertEquals(2, groups.size)
        assertEquals(listOf("a"), groups[0].products.map { it.id })
        assertEquals(listOf("b", "c"), groups[1].products.map { it.id })
    }

    @Test
    fun `PriceEntry_combine は商品グループとチャネルの明細から価格を計算する`() {
        val group = ProductGroup(ProductGroupId("G1"), "グループ1", listOf(Product("a", "商品A", 150)))
        val channel = SalesChannel(SalesChannelId("C1"), "チャネル1", listOf(Region("x", "地域X", 4)))

        val entry = PriceEntry.combine(group, group.products[0], channel, channel.regions[0])

        assertEquals("G1:a-C1:x", entry.code)
        assertEquals(150 * 4, entry.amount)
        assertEquals(ProductGroupId("G1"), entry.productGroupId)
        assertEquals(SalesChannelId("C1"), entry.salesChannelId)
    }
}
