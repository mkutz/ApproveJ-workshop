package org.approvej.workshop.service.adapters.demanding.http.article

import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArticleDtoTest {

  @Test
  fun `constructor article`() {
    val article = anArticle().build()

    val dto = ArticleDto(article)

    assertThat(dto.id).isEqualTo(article.id.toString())
    assertThat(dto.articleNumber).isEqualTo(article.articleNumber)
    assertThat(dto.title).isEqualTo(article.title)
    assertThat(dto.description).isEqualTo(article.description)
    assertThat(dto.imageUrl).isEqualTo(article.imageUrl)
    assertThat(dto.pricePerUnit).isEqualTo(299)
    assertThat(dto.pricePerUnitFormatted).isEqualTo("2.99 EUR")
  }
}
