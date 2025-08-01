package com.example.demotmi.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.data.annotation.Transient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Table(name = "rule")
data class Rule (

    @Id
    @field:Column("id")
    private val id: UUID,

    @field:Column("agent_rule_id")
    val agentRuleId: Int,

    @field:Column("agent_type")
    val agentType: String,

    @field:Column("agent_id")
    val agentId: Int,

    @field:Column("device_name")
    val deviceName: String?,

    @field:Column("device_id")
    val deviceId: Int?,

    @field:Column("device_address")
    val deviceAddress: String?,

    @field:Column("active")
    val active: Boolean = true,
    ) : Persistable<UUID> {

        @Transient
        @field:Column("isNewEntity")
        private var isNewEntity: Boolean = false

        override fun getId() = id

        @Transient
        override fun isNew() = isNewEntity

        fun asNew(): Rule {
            this.isNewEntity = true
            return this
        }
    }

@Repository
interface RuleRepository : ReactiveCrudRepository<Rule, UUID> {

    @Query("select * from rule where id = :id")
    override fun findById(id: UUID): Mono<Rule>

}