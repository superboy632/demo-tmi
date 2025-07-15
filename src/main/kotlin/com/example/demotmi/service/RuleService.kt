package com.example.demotmi.service

import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import com.example.demotmi.request.RuleCreateRequest
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.helpers.Reporter.info
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

interface RuleService {

    suspend fun create(ruleCreateRequest: RuleCreateRequest): Rule

    suspend fun delete(ruleId: UUID)

    suspend fun findById(id: UUID): Rule?

}

@Service
class DefaultRuleService(
    private val delegate: TransactionalRuleService,
    private val ruleMapper: RuleMapper
) : RuleService {

    override suspend fun create(ruleCreateRequest: RuleCreateRequest): Rule {
        val rule = ruleMapper.fromRequest(ruleCreateRequest)
            .copy(id = UUID.randomUUID())
//        val rule = Rule(
//            UUID.randomUUID(),
//            ruleCreateRequest.agentRuleId,
//            ruleCreateRequest.agentType,
//            ruleCreateRequest.agentId,
//            ruleCreateRequest.deviceName,
//            ruleCreateRequest.deviceId,
//            ruleCreateRequest.deviceAddress
//        )
        val saved = delegate.create(rule)
            .awaitSingle()
        logger { info("Rule saved: $saved") }
        return saved
    }

    override suspend fun delete(ruleId: UUID) {
        val marked = delegate.delete(ruleId)
            .awaitSingleOrNull()
        logger { info("Rule deleted: $marked") }
    }

    override suspend fun findById(id: UUID): Rule? {
        val rule = delegate.findById(id)
            .awaitSingle()
        return rule
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
}