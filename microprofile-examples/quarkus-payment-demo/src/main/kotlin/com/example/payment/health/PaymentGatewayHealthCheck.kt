package com.example.payment.health

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness

@Liveness
@ApplicationScoped
class PaymentGatewayHealthCheck : HealthCheck {

    override fun call(): HealthCheckResponse {
        return HealthCheckResponse.named("payment-gateway")
            .up()
            .withData("mode", "embedded-mock")
            .build()
    }
}
