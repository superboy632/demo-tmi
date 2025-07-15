package com.example.demotmi.controller

import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import com.example.demotmi.request.RuleCreateRequest
import com.example.demotmi.response.RuleResponse
import com.example.demotmi.service.RuleService
import kotlinx.coroutines.coroutineScope
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID


@Validated
@RestController
@RequestMapping("/rules")
class RuleController (
    private val ruleRepository: RuleRepository,
    private val ruleService: RuleService
) {

    @PostMapping
    suspend fun create(@RequestBody request: RuleCreateRequest) : ResponseEntity<RuleResponse> = coroutineScope {
        val rule = Rule(
            id = UUID.randomUUID(),
            agentRuleId = request.agentRuleId,
            agentType = request.agentType,
            agentId = request.agentId,
            deviceName = request.deviceName,
            deviceId = request.deviceId,
            deviceAddress = request.deviceAddress,
        )

        val result = ruleService.create(rule)
        ResponseEntity.ok(result.toDto())
    }

    @GetMapping("/get")
    fun getAll(): Flux<Rule> = ruleRepository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: UUID): Mono<RuleResponse> = ruleRepository.findById(id).map { i -> i.toDto() }


    @DeleteMapping("/{id}/delete")
    suspend fun deleteById(@PathVariable("id") id: UUID): ResponseEntity<Unit> = coroutineScope {
        ruleService.delete(id)
        ResponseEntity.noContent().build()
    }


    fun Rule.toDto(): RuleResponse = RuleResponse(
        id = this.id,
        agentRuleId = this.agentRuleId,
        agentType = this.agentType,
        agentId = this.agentId,
        deviceName = this.deviceName,
        deviceId = this.deviceId,
        deviceAddress = this.deviceAddress,
    )
}


