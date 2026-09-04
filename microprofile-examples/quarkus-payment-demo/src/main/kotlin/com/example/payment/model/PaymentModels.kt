package com.example.payment.model

data class PaymentRequest(
    val orderId: String,
    val amount: Long,
)

data class PaymentResult(
    val status: String,
    val transactionId: String,
    val message: String,
)
