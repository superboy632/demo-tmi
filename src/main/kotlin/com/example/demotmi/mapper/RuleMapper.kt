package com.example.demotmi.mapper

import com.example.demotmi.kafka.CreateRuleCommand
import com.example.demotmi.persistence.Rule
import com.example.demotmi.request.RuleCreateRequest
import com.example.demotmi.response.RuleResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy

@Mapper (
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface RuleMapper {

    fun toResponse(rule: Rule): RuleResponse

    fun toRuleCreateRequest(dto: CreateRuleCommand): RuleCreateRequest

    @Mapping(target = "id", ignore = true )
    fun fromRequest(dto: RuleCreateRequest): Rule
}
