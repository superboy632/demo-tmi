package com.example.demotmi.kafka

import java.util.UUID

data class CreateRuleCommand (
    val agentId: Int,
    val agentType: String
)

data class DeleteRuleCommand (
    val id: UUID,
    val agentType: String
)