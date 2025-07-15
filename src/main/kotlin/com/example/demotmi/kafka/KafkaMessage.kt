package com.example.demotmi.kafka

import java.util.UUID

data class KafkaMessage<T> (
   val type: String,
    val message: T
)


data class CreateRuleCommand (
    val id: UUID,
    val agentId: Int,
    val agentType: String
)

data class DeleteRuleCommand (
    val id: UUID,
    val agentType: String
)