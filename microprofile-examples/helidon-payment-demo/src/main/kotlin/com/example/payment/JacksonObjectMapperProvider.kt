package com.example.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jakarta.ws.rs.ext.ContextResolver
import jakarta.ws.rs.ext.Provider

@Provider
class JacksonObjectMapperProvider : ContextResolver<ObjectMapper> {

    private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    override fun getContext(type: Class<*>?): ObjectMapper = mapper
}
