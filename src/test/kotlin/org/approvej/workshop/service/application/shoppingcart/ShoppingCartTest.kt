package org.approvej.workshop.service.application.shoppingcart

import java.math.BigDecimal
import java.util.UUID.randomUUID
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.approvej.workshop.service.application.shoppingcart.ItemBuilder.Companion.anItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShoppingCartTest {

  @Test
  fun `value multiple items`() {
    val item1 = anItem().article(anArticle().price("2.5").build()).quantity(1).build()
    val item2 = anItem().article(anArticle().price("7.5").build()).quantity(1).build()
    val shoppingCart = ShoppingCart(randomUUID(), listOf(item1, item2))

    assertThat(shoppingCart.value).isEqualTo(item1.priceTotal.plus(item2.priceTotal))
  }

  @Test
  fun `value one item`() {
    val item = anItem().article(anArticle().price("2.5").build()).quantity(5).build()
    val shoppingCart = ShoppingCart(randomUUID(), listOf(item))

    assertThat(shoppingCart.value).isEqualTo(item.priceTotal)
  }

  @Test
  fun `value no items`() {
    val shoppingCart = ShoppingCart(randomUUID(), emptyList())

    assertThat(shoppingCart.value).isEqualTo(BigDecimal.ZERO)
  }
}
