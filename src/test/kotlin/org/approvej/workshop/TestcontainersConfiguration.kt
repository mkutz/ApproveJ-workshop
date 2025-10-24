package org.approvej.workshop

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import net.ttddyy.dsproxy.ExecutionInfo
import net.ttddyy.dsproxy.QueryInfo
import net.ttddyy.dsproxy.listener.QueryExecutionListener
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait.forListeningPort
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

  @Component
  data class QueryCollector(private val queries: MutableList<QueryInfo> = mutableListOf()) :
    QueryExecutionListener {

    override fun beforeQuery(execInfo: ExecutionInfo?, queryInfoList: List<QueryInfo>) {
      queries.addAll(queryInfoList)
    }

    override fun afterQuery(execInfo: ExecutionInfo?, queryInfoList: List<QueryInfo?>?) = Unit

    fun queries(): List<QueryInfo> = queries

    fun reset() {
      queries.clear()
    }
  }

  @Bean
  fun postgresContainer(): PostgreSQLContainer<*> =
    PostgreSQLContainer(DockerImageName.parse("postgres:latest")).waitingFor(forListeningPort())

  @Bean
  fun originalDataSource(postgresContainer: PostgreSQLContainer<*>): DataSource =
    HikariDataSource().apply {
      jdbcUrl = postgresContainer.jdbcUrl
      username = postgresContainer.username
      password = postgresContainer.password
      driverClassName = postgresContainer.driverClassName
    }

  @Bean
  @Primary
  fun dataSourceProxy(queryCollector: QueryCollector, originalDataSource: DataSource): DataSource {
    return ProxyDataSourceBuilder.create(originalDataSource)
      .name("datasourceProxy")
      .listener(queryCollector)
      .build()
  }

  @Bean @ServiceConnection fun kafkaContainer() = KafkaContainer("apache/kafka-native:latest")
}
