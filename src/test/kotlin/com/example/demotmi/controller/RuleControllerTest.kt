package com.example.demotmi.controller

import com.example.demotmi.Fixtures
import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.service.RuleService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.server.ResponseStatusException

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [
        RuleController::class,
        DefaultExceptionHandler::class
    ]
)
@EnableAutoConfiguration(
    exclude = [
        LiquibaseAutoConfiguration::class
    ]
)
class RuleControllerTest {
    @MockitoBean
    private lateinit var ruleMapper: RuleMapper

    @MockitoBean
    private lateinit var rules: RuleService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoSpyBean
    private lateinit var defaultExceptionHandler: DefaultExceptionHandler

    @Test
    fun shouldGetRuleById() = runTest {
        val rule = Fixtures.rule()
        val response = Fixtures.createRuleResponse(rule)

        whenever(rules.findById(rule.id)).thenReturn(rule)
        whenever(ruleMapper.toResponse(rule)).thenReturn(response)

        webTestClient.get()
            .uri("/rules/get/${rule.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(rule.id.toString())
    }

    @Test
    fun shouldCreateRule() = runTest {
        val request = Fixtures.ruleCreateRequest()
        val rule = Fixtures.rule()
        val response = Fixtures.createRuleResponse(rule)

        whenever(rules.create(request)).thenReturn(rule)
        whenever(ruleMapper.toResponse(rule)).thenReturn(response)

        webTestClient.post()
            .uri("/rules/create")
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.agentRuleId").isEqualTo(rule.agentRuleId.toString())
    }

    @Test
    fun shouldUpdateRule() = runTest {
        val rule = Fixtures.rule()
        val response = Fixtures.createRuleResponse(rule)

        whenever(rules.update(rule.id)).thenReturn(rule)
        whenever(ruleMapper.toResponse(rule)).thenReturn(response)

        webTestClient.patch()
            .uri("rules/{id}/toggle-active", rule.id)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(rule.id.toString())
    }

    @Test
    fun shouldDeleteRule() = runTest {
        val rule = Fixtures.rule()

        whenever(rules.delete(rule.id)).thenReturn(null)

        webTestClient.delete()
            .uri("rules/delete/{id}", rule.id)
            .exchange()
            .expectStatus().isNoContent
            .expectBody()
    }

    @Test
    @Suppress("ReactiveStreamsUnusedPublisher")
    fun shouldReceiveBadRequest() {
        webTestClient.delete()
            .uri("/rules/delete/123")
            .exchange()
            .expectStatus().isBadRequest

        verify(defaultExceptionHandler)
            .handle(any(), any<ResponseStatusException>())
    }
}