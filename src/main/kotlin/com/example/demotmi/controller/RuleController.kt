package com.example.demotmi.controller

import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import com.example.demotmi.request.RuleCreateRequest
import com.example.demotmi.response.RuleResponse
import com.example.demotmi.service.RuleService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
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
    private val ruleService: RuleService,
    private val ruleMapper: RuleMapper
) {

    @PostMapping("/create")
    suspend fun create(@RequestBody request: RuleCreateRequest) : ResponseEntity<RuleResponse> = coroutineScope {
        val result = ruleService.create(request)
        val response = ruleMapper.toResponse(result)
        ResponseEntity(response, HttpStatus.CREATED)

    }


    @GetMapping("/get/{id}")
    suspend fun getById(@PathVariable("id") id: UUID): ResponseEntity<RuleResponse> = coroutineScope {
        val rule = ruleService.findById(id)
            ?: throw NoSuchElementException("Rule with this id=$id not found")
        val response = ruleMapper.toResponse(rule)
        ResponseEntity.ok(response)
    }

    @DeleteMapping("/delete/{id}")
    suspend fun deleteById(@PathVariable("id") id: UUID): ResponseEntity<Unit> = coroutineScope {
        ruleService.delete(id)
        ResponseEntity.noContent().build()
    }

}


