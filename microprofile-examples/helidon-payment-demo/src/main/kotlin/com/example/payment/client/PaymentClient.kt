package com.example.payment.client

import com.example.payment.model.PaymentRequest
import com.example.payment.model.PaymentResult
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "payment-gateway")
@Path("/gateway")
interface PaymentClient {

    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun pay(request: PaymentRequest): PaymentResult
}
