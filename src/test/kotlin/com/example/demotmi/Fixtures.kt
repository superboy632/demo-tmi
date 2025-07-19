package com.example.demotmi

import com.example.demotmi.kafka.CreateRuleCommand
import com.example.demotmi.kafka.DeleteRuleCommand
import com.example.demotmi.kafka.KafkaMessage
import com.example.demotmi.persistence.Rule
import com.example.demotmi.request.RuleCreateRequest
import com.example.demotmi.response.RuleResponse
import java.util.UUID

object Fixtures {

    fun ruleCreateRequest(): RuleCreateRequest =
        RuleCreateRequest(
            agentRuleId = 123,
            agentType = "test",
            agentId = 123,
            deviceName = "test",
            deviceId = 42,
            deviceAddress = "test",
        )

    fun rule(): Rule =
        Rule(UUID.randomUUID(),
            123,
            "string",
            123,
            null,
            null,
            null,
            true)

    fun createRuleCommand(): CreateRuleCommand =
        CreateRuleCommand(agentId = 123, agentType = "test")

    fun createKafkaMessage(): KafkaMessage<CreateRuleCommand> =
        KafkaMessage(
            type = "create",
            message = createRuleCommand(),
        )

    fun deleteRuleCommand(): DeleteRuleCommand =
        DeleteRuleCommand(id = UUID.randomUUID(), agentType = "test")

    fun deleteKafkaMessage(): KafkaMessage<DeleteRuleCommand> =
        KafkaMessage(
            type = "delete",
            message = deleteRuleCommand(),
        )

    fun createRuleResponse(rule: Rule): RuleResponse =
        RuleResponse(
            rule.id,
            rule.agentRuleId,
            rule.agentType,
            rule.agentId,
            rule.deviceName,
            rule.deviceId,
            rule.deviceAddress,
            active = true
        )
}