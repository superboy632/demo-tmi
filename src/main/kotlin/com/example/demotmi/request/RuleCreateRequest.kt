package com.example.demotmi.request

data class RuleCreateRequest (
    val agentRuleId: Int,

    val agentType: String,

    val agentId: Int,

    val deviceName: String,

    val deviceId: Int?,

    val deviceAddress: String?,
)