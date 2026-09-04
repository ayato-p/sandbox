package com.example.payment.resource

import com.example.payment.model.PaymentRequest
import com.example.payment.model.PaymentResult
import com.example.payment.service.PaymentService
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@Path("/api/payments")
@Tag(name = "Payments")
class PaymentResource @Inject constructor(
    private val paymentService: PaymentService,
) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Process a payment via the external gateway")
    fun pay(request: PaymentRequest): PaymentResult {
        return paymentService.pay(request)
    }
}
