package com.example.demotmi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [
        DataSourceTransactionManagerAutoConfiguration::class
    ]
)
class DemoTmiApplication

fun main(args: Array<String>) {
    runApplication<DemoTmiApplication>(*args)
}
