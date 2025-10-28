package org.approvej.workshop.service.adapters.demanding.kafka.article

import java.math.BigDecimal
import java.util.UUID.randomUUID
import org.approvej.workshop.service.application.article.Article
import org.approvej.workshop.service.application.article.UnknownQuantityUnitException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ArticleMessageTest {

  private val validMessage =
    ArticleMessage(
      id = randomUUID().toString(),
      articleNumber = "A-BCD-123-456",
      title = "Some title",
      description = "Some description",
      imageUrl = "https://approvej.org/images/${randomUUID()}",
      pricePerUnit = "2.99",
      quantityPerUnit = "1.5 l",
    )

  @Test
  fun toArticle() {
    val message = validMessage

    val article = message.toArticle()

    assertThat(article.id.toString()).isEqualTo(message.id)
    assertThat(article.articleNumber).isEqualTo(message.articleNumber)
    assertThat(article.title).isEqualTo(message.title)
    assertThat(article.imageUrl).isEqualTo(message.imageUrl)
    assertThat(article.description).isEqualTo(message.description)
    assertThat(article.imageUrl).isEqualTo(message.imageUrl)
    assertThat(article.pricePerUnit).isEqualTo(BigDecimal(message.pricePerUnit))
    assertThat(article.quantityPerUnit.value).isEqualTo(BigDecimal(1.5))
    assertThat(article.quantityPerUnit.unit).isEqualTo(Article.QuantityUnit.LITER)
  }

  @Test
  fun `toArticle invalid quantity string`() {
    val message = validMessage.copy(quantityPerUnit = "a lot")

    assertThatThrownBy { message.toArticle() }
      .isInstanceOf(InvalidQuantityStringException::class.java)
      .hasMessage("Invalid quantity string: a lot")
  }

  @Test
  fun `toArticle unknown quantity unit`() {
    val message = validMessage.copy(quantityPerUnit = "1t")

    assertThatThrownBy { message.toArticle() }
      .isInstanceOf(UnknownQuantityUnitException::class.java)
      .hasMessage("Unknown quantity unit: t")
  }
}
