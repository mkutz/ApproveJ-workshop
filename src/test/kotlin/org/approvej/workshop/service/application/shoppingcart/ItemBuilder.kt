package org.approvej.workshop.service.application.shoppingcart

import org.approvej.workshop.service.application.article.Article
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle

class ItemBuilder private constructor() {

  var article: Article = anArticle().build()
  var quantity: Int = 1

  companion object {
    fun anItem(): ItemBuilder = ItemBuilder()
  }

  fun article(article: Article) = apply { this.article = article }

  fun quantity(quantity: Int) = apply { this.quantity = quantity }

  fun build() = Item(article, quantity)
}
