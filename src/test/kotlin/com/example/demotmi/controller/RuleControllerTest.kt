package com.example.demotmi.controller

import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import com.example.demotmi.request.RuleCreateRequest
import com.example.demotmi.response.RuleResponse
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
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.server.ResponseStatusException
import java.util.UUID


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
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
        val rule = Rule(
            id = UUID.randomUUID(),
            agentRuleId = 101,
            agentType = "BOT",
            agentId = 55,
            deviceName = "Sensor-X",
            deviceId = 9001,
            deviceAddress = "192.168.0.101"
        )
        val response = RuleResponse(
            rule.id,
            rule.agentRuleId,
            rule.agentType,
            rule.agentId,
            rule.deviceName,
            rule.deviceId,
            rule.deviceAddress,
            active = true
        )

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
        val request = RuleCreateRequest(agentRuleId = 101,
            agentType = "BOT",
            agentId = 55,
            deviceName = "Sensor-X",
            deviceId = 9001,
            deviceAddress = "192.168.0.101")
        val rule = Rule(
            id = UUID.randomUUID(),
            agentRuleId = 101,
            agentType = "BOT",
            agentId = 55,
            deviceName = "Sensor-X",
            deviceId = 9001,
            deviceAddress = "192.168.0.101"
        )
        val response = RuleResponse(
            rule.id,
            rule.agentRuleId,
            rule.agentType,
            rule.agentId,
            rule.deviceName,
            rule.deviceId,
            rule.deviceAddress,
            active = true
        )

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

        val rule = Rule(
            id = UUID.randomUUID(),
            agentRuleId = 101,
            agentType = "BOT",
            agentId = 55,
            deviceName = "Sensor-X",
            deviceId = 9001,
            deviceAddress = "192.168.0.101"
        )
        val response = RuleResponse(
            rule.id,
            rule.agentRuleId,
            rule.agentType,
            rule.agentId,
            rule.deviceName,
            rule.deviceId,
            rule.deviceAddress,
            active = true
        )

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

        val rule = Rule(
            id = UUID.randomUUID(),
            agentRuleId = 101,
            agentType = "BOT",
            agentId = 55,
            deviceName = "Sensor-X",
            deviceId = 9001,
            deviceAddress = "192.168.0.101"
        )

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