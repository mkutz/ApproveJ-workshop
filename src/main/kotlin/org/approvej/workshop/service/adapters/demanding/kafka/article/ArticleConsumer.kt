package org.approvej.workshop.service.adapters.demanding.kafka.article

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.UUID
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.approvej.workshop.service.application.article.ToManagerArticles
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ArticleConsumer(
  private val articleImporter: ToManagerArticles,
  private val objectMapper: ObjectMapper,
) {

  @KafkaListener(topics = ["article"])
  fun consumeArticle(record: ConsumerRecord<UUID, String?>) {
    val value = record.value()
    if (value == null) {
      articleImporter.deleteArticle(record.key())
    } else {
      val articleMessage: ArticleMessage = objectMapper.readValue<ArticleMessage>(value)
      articleImporter.importArticle(articleMessage.toArticle())
    }
  }
}
