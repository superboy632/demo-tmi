package com.example.demotmi.service

import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.helpers.Reporter.info
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

interface RuleService {

    suspend fun create(rule: Rule): Rule

    suspend fun delete(ruleId: UUID)


}

@Service
class DefaultRuleService(
    private val rules: RuleRepository,
    private val delegate: TransactionalRuleService
) : RuleService {

    override suspend fun create(rule: Rule): Rule {
        val saved = rules.save(rule)
            .awaitSingle()

        logger { info("Rule created: $saved") }
        return saved as Rule
    }

    override suspend fun delete(ruleId: UUID) {
        val marked = delegate.delete(ruleId).awaitSingleOrNull()
        logger { info("Rule deleted: $marked") }
    }

    @Service
    class TransactionalRuleService (
        private val rules: RuleRepository
    ) {
        @Transactional
        fun delete(id: UUID): Mono<Void> = rules.deleteById(id)


    }
}