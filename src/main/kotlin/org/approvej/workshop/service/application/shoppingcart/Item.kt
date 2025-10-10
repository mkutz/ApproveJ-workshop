package org.approvej.workshop.service.application.shoppingcart

import java.math.BigDecimal
import java.time.Instant
import java.util.*
import org.approvej.workshop.service.application.article.Article

data class Item(
  val id: UUID,
  val articleId: UUID,
  val articleNumber: String,
  val title: String,
  val imageUrl: String,
  val quantity: Int,
  val pricePerUnit: BigDecimal,
  val quantityPerUnit: Article.Quantity,
  val insertionTime: Instant,
) {

  val priceTotal: BigDecimal = pricePerUnit.multiply(quantity.toBigDecimal())
  val quantityTotal =
    Article.Quantity(quantityPerUnit.value.multiply(quantity.toBigDecimal()), quantityPerUnit.unit)

  constructor(
    article: Article,
    quantity: Int,
  ) : this(
    id = UUID.randomUUID(),
    articleId = article.id,
    articleNumber = article.articleNumber,
    title = article.title,
    imageUrl = article.imageUrl,
    quantity = quantity,
    pricePerUnit = article.pricePerUnit,
    quantityPerUnit = article.quantityPerUnit,
    insertionTime = Instant.now(),
  )
}
