package com.example.payment.resource

import com.example.payment.model.PaymentRequest
import com.example.payment.model.PaymentResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Path("/gateway")
@ApplicationScoped
class GatewayResource @Inject constructor(
    @ConfigProperty(name = "payment.gateway.simulate-failures", defaultValue = "0")
    private val simulateFailures: Int,
) {
    private val attemptCounts = ConcurrentHashMap<String, AtomicInteger>()

    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun pay(request: PaymentRequest): PaymentResult {
        if (simulateFailures > 0) {
            val attempts = attemptCounts.computeIfAbsent(request.orderId) { AtomicInteger(0) }
            if (attempts.incrementAndGet() <= simulateFailures) {
                throw WebApplicationException(
                    "Gateway temporarily unavailable",
                    Response.status(Response.Status.SERVICE_UNAVAILABLE).build(),
                )
            }
        }

        return PaymentResult(
            status = "COMPLETED",
            transactionId = UUID.randomUUID().toString(),
            message = "Payment processed for order ${request.orderId}",
        )
    }
}
