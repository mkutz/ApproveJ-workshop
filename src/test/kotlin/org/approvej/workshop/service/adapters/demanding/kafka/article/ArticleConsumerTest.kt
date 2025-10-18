package org.approvej.workshop.service.adapters.demanding.kafka.article

import java.util.UUID.randomUUID
import org.approvej.workshop.TestcontainersConfiguration
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.approvej.workshop.service.application.article.ToStoreArticles
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.untilNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.KafkaTemplate

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleConsumerTest(
  @param:Autowired val articleStore: ToStoreArticles,
  @param:Autowired val kafkaTemplate: KafkaTemplate<String, String>,
) {

  @Test
  fun consumeArticle() {
    val articleId = randomUUID()
    val articleMessage =
      """
      {
        "id": "$articleId",
        "articleNumber": "A-BCD-123-456",
        "title": "Catapult",
        "description": "Not suitable for coyote transportation!",
        "imageUrl": "https://approvej.org/images/$articleId",
        "pricePerUnit": "479.99",
        "quantityPerUnit": "1"
      }
    """
        .trimIndent()

    kafkaTemplate.send("article", "$articleId", articleMessage).get()

    val storedArticle = await().untilNotNull { articleStore.getArticleById(articleId) }
    assertThat(storedArticle.id).isEqualTo(articleId)
  }

  @Test
  fun consumeArticle_invalid() {
    val invalidArticleId = randomUUID()
    val invalidArticleMessage =
      """
      {
        "id": "$invalidArticleId",
        "invalid": "1"
      }
    """
        .trimIndent()

    val validArticleId = randomUUID()
    val validArticleMessage =
      """
      {
        "id": "$validArticleId",
        "articleNumber": "A-BCD-123-456",
        "title": "Catapult",
        "description": "Not suitable for coyote transportation!",
        "imageUrl": "https://approvej.org/images/$validArticleId",
        "pricePerUnit": "479.99",
        "quantityPerUnit": "1"
      }
    """
        .trimIndent()

    kafkaTemplate.send("article", "$invalidArticleId", invalidArticleMessage).get()
    kafkaTemplate.send("article", "$validArticleId", validArticleMessage).get()

    await().untilAsserted { assertThat(articleStore.getArticleById(validArticleId)).isNotNull() }
    assertThat(articleStore.getArticleById(invalidArticleId)).isNull()
  }

  @Test
  fun consumeArticle_tombstone() {
    val storedArticle = articleStore.storeArticle(anArticle().build())

    kafkaTemplate.send("article", "${storedArticle.id}", null).get()

    await().untilAsserted { assertThat(articleStore.getArticleById(storedArticle.id)).isNull() }
  }
}
