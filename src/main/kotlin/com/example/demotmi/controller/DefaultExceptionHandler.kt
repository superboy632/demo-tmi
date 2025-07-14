package com.example.demotmi.controller

//import io.github.oshai.kotlinlogging.KLogging
//import org.reactivestreams.Publisher
//import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
//import org.springframework.core.annotation.Order
//import org.springframework.core.io.buffer.DataBuffer
//import org.springframework.core.io.buffer.DataBufferFactory
//import org.springframework.http.HttpStatus
//import org.springframework.stereotype.Component
//import org.springframework.web.bind.support.WebExchangeBindException
//import org.springframework.web.server.ResponseStatusException
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//import io.github.oshai.kotlinlogging.KotlinLogging
//import reactor.core.publisher.Flux
//
//@Component
//@Order(-2)
//class DefaultExceptionHandler : ErrorWebExceptionHandler {
//
//    private val logger = KotlinLogging.logger {}
//
//    override fun handle(exchange: ServerWebExchange, t: Throwable): Mono<Void> {
//
//        when (t) {
//            is WebExchangeBindException -> {
//                logger.error { "Validation error: ${t.localizedMessage}" }
//                exchange.response.statusCode = HttpStatus.BAD_REQUEST
//            }
//
//            is ResponseStatusException -> {
//                logger.error{"Response status error: ${t.localizedMessage}" }
//                exchange.response.statusCode = HttpStatus.BAD_REQUEST
//            }
//
//            is NoSuchElementException -> {
//                logger.error{"Entity not found: ${t.localizedMessage}" }
//                exchange.response.statusCode = HttpStatus.BAD_REQUEST
//            }
//
//            is IllegalArgumentException -> {
//                logger.error{"Entity already exists: ${t.localizedMessage}" }
//                exchange.response.statusCode = HttpStatus.BAD_REQUEST
//            }
//
//            is IllegalStateException -> {
//                logger.error{"Rule manipulation error:  ${t.localizedMessage}" }
//                exchange.response.statusCode = HttpStatus.BAD_REQUEST
//            }
//
//            else -> {
//                logger.error{"Internal server error: ${t.localizedMessage}" }
//                exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
//            }
//        }
//
//
//        return exchange.response.writeWith(Mono)
//    }
//
//    companion object : KLogging()
//}