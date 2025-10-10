package org.approvej.workshop.service.application.shoppingcart

import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ItemTest {

  @Test
  fun `constructor article quantity`() {
    val article = anArticle().build()
    val quantity = 1

    val item = Item(article, quantity)

    assertThat(item.id).isNotNull
    assertThat(item.articleId).isEqualTo(article.id)
    assertThat(item.title).isEqualTo(article.title)
    assertThat(item.pricePerUnit).isEqualTo(article.pricePerUnit)
    assertThat(item.imageUrl).isEqualTo(article.imageUrl)
    assertThat(item.priceTotal).isEqualTo(article.pricePerUnit)
    assertThat(item.quantity).isEqualTo(quantity)
  }
}
