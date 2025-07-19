package com.example.demotmi.service

import com.example.demotmi.Fixtures
import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import reactor.core.publisher.Mono
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows


@SpringBootTest(
    classes = [
        DefaultRuleService::class,
        DefaultRuleService.TransactionalRuleService::class
    ]
)
class RuleServiceTest {

    @Autowired
    private lateinit var ruleService: RuleService

    @MockitoBean
    private lateinit var ruleRepository: RuleRepository

    @Test
    @Suppress("ReactiveStreamsUnusedPublisher")
    fun testCreateRule() = runTest {
        val request = Fixtures.ruleCreateRequest()
        val captured = argumentCaptor<Rule>()

        whenever(ruleRepository.save(captured.capture()))
            .thenAnswer { invocation ->
                Mono.just(invocation.arguments[0] as Rule)
            }

        ruleService.create(request)
        verify(ruleRepository).save(any())
        with(captured.firstValue) {
            assertEquals(request.agentRuleId, agentRuleId)
            assertEquals(request.agentType, agentType)
            assertEquals(request.agentId, agentId)
            assertEquals(request.deviceName, deviceName)
            assertEquals(request.deviceId, deviceId)
            assertEquals(request.deviceAddress, deviceAddress)
            assertTrue(active)
        }
    }

    @Test
    fun testFindRule() = runTest {
        val id = UUID.randomUUID()
        val rule = Fixtures.rule()

        whenever(ruleRepository.findById(id)).thenReturn(Mono.just(rule))

        val result = ruleService.findById(id)
        assertEquals(rule, result)
    }
    @Test
    fun testFindRuleNoSuchElement() = runTest {
        val id = UUID.randomUUID()

        whenever(ruleRepository.findById(id)).thenReturn(Mono.empty())

        val exception = assertThrows<NoSuchElementException> {
            ruleService.findById(id)
        }
        assertEquals("Rule with id $id not found", exception.message)
    }

    @Test
    fun testUpdateRuleNoSuchElement() = runTest {
        val id = UUID.randomUUID()

        whenever(ruleRepository.findById(id)).thenReturn(Mono.empty())

        val exception = assertThrows<NoSuchElementException> {
            ruleService.update(id)
        }
        assertEquals("Rule with id $id not found", exception.message)
    }

    @Test
    fun testDeleteRuleByIdNoSuchElement() = runTest {
        val id = UUID.randomUUID()

        whenever(ruleRepository.findById(id)).thenReturn(Mono.empty())
        whenever(ruleRepository.deleteById(id)).thenReturn(Mono.empty())

        val exception = assertThrows<NoSuchElementException> {
            ruleService.delete(id)
        }
        assertEquals("Rule with id $id not found", exception.message)
    }

    @Test
    @Suppress("ReactiveStreamsUnusedPublisher")
    fun testDeleteRuleById() = runTest {
        val id = UUID.randomUUID()
        val rule = Fixtures.rule()

        whenever(ruleRepository.findById(id)).thenReturn(Mono.just(rule))
        whenever(ruleRepository.deleteById(id)).thenReturn(Mono.empty())

        ruleService.delete(id)

        verify(ruleRepository).findById(id)
        verify(ruleRepository).deleteById(id)
    }
}