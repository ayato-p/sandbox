package com.example.cleanarch.usecase

import com.example.cleanarch.domain.PriceEntry
import com.example.cleanarch.domain.Product
import com.example.cleanarch.domain.ProductGroupHead
import com.example.cleanarch.domain.ProductGroupId
import com.example.cleanarch.domain.Region
import com.example.cleanarch.domain.SalesChannelHead
import com.example.cleanarch.domain.SalesChannelId
import com.example.cleanarch.usecase.port.PriceEntryPort
import com.example.cleanarch.usecase.port.ProductGroupHeadPort
import com.example.cleanarch.usecase.port.ProductPort
import com.example.cleanarch.usecase.port.RegionPort
import com.example.cleanarch.usecase.port.SalesChannelHeadPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GeneratePriceListUseCaseTest {

    // --- インメモリの Fake ポート（Gateway 実装の代わり） ---

    private class FakeProductGroupHeadPort(private val heads: List<ProductGroupHead>) : ProductGroupHeadPort {
        override fun findAll(): List<ProductGroupHead> = heads
    }

    private class FakeProductPort(private val products: Map<ProductGroupId, List<Product>>) : ProductPort {
        override fun findByGroup(groupId: ProductGroupId): List<Product> = products[groupId].orEmpty()
    }

    private class FakeSalesChannelHeadPort(private val heads: List<SalesChannelHead>) : SalesChannelHeadPort {
        override fun findAll(): List<SalesChannelHead> = heads
    }

    private class FakeRegionPort(private val regions: Map<SalesChannelId, List<Region>>) : RegionPort {
        override fun findByChannel(channelId: SalesChannelId): List<Region> = regions[channelId].orEmpty()
    }

    /** 保存された価格エントリを順番に貯める Fake。逐次保存されたことを確認できる。 */
    private class RecordingPriceEntryPort : PriceEntryPort {
        val saved = mutableListOf<PriceEntry>()
        override fun save(priceEntry: PriceEntry) {
            saved += priceEntry
        }
    }

    @Test
    fun `商品グループと販売チャネルのあらゆる組み合わせから価格エントリが生成され逐次保存される`() {
        // 商品グループ: G1(商品 a,b), G2(商品 c) → 商品は合計 3
        val groupHeads = listOf(
            ProductGroupHead(ProductGroupId("G1"), "グループ1"),
            ProductGroupHead(ProductGroupId("G2"), "グループ2"),
        )
        val products = mapOf(
            ProductGroupId("G1") to listOf(
                Product("a", "商品A", basePrice = 100),
                Product("b", "商品B", basePrice = 200),
            ),
            ProductGroupId("G2") to listOf(
                Product("c", "商品C", basePrice = 300),
            ),
        )
        // 販売チャネル: C1(地域 x,y), C2(地域 z) → 地域は合計 3
        val channelHeads = listOf(
            SalesChannelHead(SalesChannelId("C1"), "チャネル1"),
            SalesChannelHead(SalesChannelId("C2"), "チャネル2"),
        )
        val regions = mapOf(
            SalesChannelId("C1") to listOf(
                Region("x", "地域X", rate = 2),
                Region("y", "地域Y", rate = 3),
            ),
            SalesChannelId("C2") to listOf(
                Region("z", "地域Z", rate = 5),
            ),
        )

        val priceEntryPort = RecordingPriceEntryPort()
        val useCase = GeneratePriceListUseCase(
            productGroupHeadPort = FakeProductGroupHeadPort(groupHeads),
            productPort = FakeProductPort(products),
            salesChannelHeadPort = FakeSalesChannelHeadPort(channelHeads),
            regionPort = FakeRegionPort(regions),
            priceEntryPort = priceEntryPort,
        )

        val count = useCase.execute()

        // 4 重ループの総数 = (全商品数 3) × (全地域数 3) = 9
        assertEquals(9, count)
        assertEquals(9, priceEntryPort.saved.size)

        // 組み合わせが重複なく網羅されている
        assertEquals(9, priceEntryPort.saved.map { it.code }.toSet().size)

        // 価格 = 商品の基準価格 × 地域の係数 の代表例を検証
        val entry = priceEntryPort.saved.first { it.code == "G1:a-C1:x" }
        assertEquals(100 * 2, entry.amount)

        val entry2 = priceEntryPort.saved.first { it.code == "G2:c-C2:z" }
        assertEquals(300 * 5, entry2.amount)
    }

    @Test
    fun `商品を持たない商品グループは価格エントリを生み出さない`() {
        val groupHeads = listOf(ProductGroupHead(ProductGroupId("G1"), "グループ1"))
        val products = mapOf(ProductGroupId("G1") to emptyList<Product>())
        val channelHeads = listOf(SalesChannelHead(SalesChannelId("C1"), "チャネル1"))
        val regions = mapOf(SalesChannelId("C1") to listOf(Region("x", "地域X", rate = 2)))

        val priceEntryPort = RecordingPriceEntryPort()
        val useCase = GeneratePriceListUseCase(
            productGroupHeadPort = FakeProductGroupHeadPort(groupHeads),
            productPort = FakeProductPort(products),
            salesChannelHeadPort = FakeSalesChannelHeadPort(channelHeads),
            regionPort = FakeRegionPort(regions),
            priceEntryPort = priceEntryPort,
        )

        val count = useCase.execute()

        assertEquals(0, count)
        assertTrue(priceEntryPort.saved.isEmpty())
    }
}
