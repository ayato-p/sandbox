package com.example.cleanarch.usecase.port

import com.example.cleanarch.domain.Region
import com.example.cleanarch.domain.SalesChannelHead
import com.example.cleanarch.domain.SalesChannelId

/**
 * 販売チャネルを導出するための専用ポート **その1**（ヘッダ取得）。
 */
interface SalesChannelHeadPort {
    fun findAll(): List<SalesChannelHead>
}

/**
 * 販売チャネルを導出するための専用ポート **その2**（対象地域の取得）。
 */
interface RegionPort {
    fun findByChannel(channelId: SalesChannelId): List<Region>
}
