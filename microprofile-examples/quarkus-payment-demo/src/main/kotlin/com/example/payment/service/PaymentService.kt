package com.example.payment.service

import com.example.payment.client.PaymentClient
import com.example.payment.model.PaymentRequest
import com.example.payment.model.PaymentResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.faulttolerance.Timeout
import org.eclipse.microprofile.rest.client.inject.RestClient

@ApplicationScoped
class PaymentService @Inject constructor(
    @RestClient private val paymentClient: PaymentClient,
    @ConfigProperty(name = "payment.timeout") private val timeout: Long,
) {

    @Retry(maxRetries = 3)
    @Timeout(1000)
    fun pay(request: PaymentRequest): PaymentResult {
        return paymentClient.pay(request)
    }

    fun configuredTimeoutMs(): Long = timeout
}
