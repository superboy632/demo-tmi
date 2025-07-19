package com.example.demotmi.service

import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import com.example.demotmi.request.RuleCreateRequest
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

interface RuleService {

    suspend fun create(ruleCreateRequest: RuleCreateRequest): Rule

    suspend fun delete(ruleId: UUID)

    suspend fun findById(id: UUID): Rule

    suspend fun update(id: UUID): Rule
}

@Service
class DefaultRuleService(
    private val delegate: TransactionalRuleService,
) : RuleService {

    override suspend fun create(ruleCreateRequest: RuleCreateRequest): Rule {
        val rule = Rule(
            UUID.randomUUID(),
            ruleCreateRequest.agentRuleId,
            ruleCreateRequest.agentType,
            ruleCreateRequest.agentId,
            ruleCreateRequest.deviceName,
            ruleCreateRequest.deviceId,
            ruleCreateRequest.deviceAddress
        ).asNew()
        val saved = delegate.create(rule)
            .awaitSingle()
        logger.info { "Rule saved: $saved" }
        return saved
    }

    override suspend fun delete(ruleId: UUID) {
        val rule = delegate.findById(ruleId)
            .awaitSingleOrNull()
            ?: throw NoSuchElementException("Rule with id $ruleId not found")
        logger.info { "Deleting rule: $rule" }
        delegate.delete(ruleId)
            .awaitSingleOrNull()
        logger.info { "Rule deleted: $ruleId " }
    }

    override suspend fun findById(id: UUID): Rule {
        val rule = delegate.findById(id)
            .awaitSingleOrNull()
            ?: throw NoSuchElementException("Rule with id $id not found")
        return rule
    }

    override suspend fun update(id: UUID): Rule {
        val rule = delegate.findById(id)
            .awaitSingleOrNull()
            ?: throw NoSuchElementException("Rule with id $id not found")
        val updated = rule.copy(active = !rule.active)
        return delegate.create(updated).awaitSingle()
    }

    @Service
    class TransactionalRuleService (
        private val rules: RuleRepository,
        private val ruleRepository: RuleRepository
    ) {

        @Transactional
        fun create(rule: Rule): Mono<Rule> = ruleRepository.save(rule)

        @Transactional
        fun delete(id: UUID): Mono<Void> = rules.deleteById(id)

        @Transactional(readOnly = true)
        fun findById(id: UUID): Mono<Rule> = ruleRepository.findById(id)
    }
    companion object : KLogging()
}