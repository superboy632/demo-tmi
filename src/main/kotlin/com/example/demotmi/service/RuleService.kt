package com.example.demotmi.service

import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import java.util.*

interface RuleService {

    suspend fun create(rule: Rule): Rule

    suspend fun delete(ruleId: UUID)


}

@Service
class DeafaultRuleService(
    private val rules: RuleRepository,
) : RuleService {

    override suspend fun create(rule: Rule): Rule {
        val saved = rules.save(rule)
            .awaitSingle()

        logger { ("Rule created: $saved") }
        return saved as Rule
    }

    override suspend fun delete(ruleId: UUID) {
        rules.deleteById(ruleId).awaitSingleOrNull()
    }
}