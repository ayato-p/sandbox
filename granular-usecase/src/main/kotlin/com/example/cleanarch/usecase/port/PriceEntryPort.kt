package com.example.cleanarch.usecase.port

import com.example.cleanarch.domain.PriceEntry

/**
 * 生成した価格エントリを出力（保存）するためのポート。
 * ユースケースは価格エントリを 1 件ずつ [save] に渡して逐次保存する。
 */
interface PriceEntryPort {
    fun save(priceEntry: PriceEntry)
}
