package com.example.practice.usecase.port

import com.example.practice.domain.Product
import com.example.practice.domain.ProductGroupHead
import com.example.practice.domain.ProductGroupId

/**
 * 商品グループを導出するための専用ポート **その1**（ヘッダ取得）。
 * 実装（Gateway）はインフラ層に置かれる想定で、ここでは境界のみ定義する。
 */
interface ProductGroupHeadPort {
    fun findAll(): List<ProductGroupHead>
}

/**
 * 商品グループを導出するための専用ポート **その2**（所属商品の取得）。
 */
interface ProductPort {
    fun findByGroup(groupId: ProductGroupId): List<Product>
}
