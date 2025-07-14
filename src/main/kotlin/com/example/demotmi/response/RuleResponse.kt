package com.example.demotmi.response

import java.util.*

data class RuleResponse (
    val id: UUID,

    val agentRuleId: Int,

    val agentType: String,

    val agentId: Int,

    val deviceName: String,

    val deviceId: Int?,

    val deviceAddress: String?

)