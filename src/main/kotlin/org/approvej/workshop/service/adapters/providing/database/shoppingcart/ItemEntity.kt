package org.approvej.workshop.service.adapters.providing.database.shoppingcart

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import org.approvej.workshop.service.application.article.Article
import org.approvej.workshop.service.application.shoppingcart.Item

@Entity
@Table(name = "item")
data class ItemEntity(
  @Id val id: UUID,
  val articleId: UUID,
  val articleNumber: String,
  val title: String,
  val imageUrl: String,
  val quantity: Int,
  val pricePerPiece: BigDecimal,
  val shoppingCartId: UUID,
  val quantityPerUnitValue: BigDecimal,
  val quantityUnit: String,
  val insertionTime: Instant,
) {

  constructor(
    item: Item
  ) : this(
    id = item.id,
    articleId = item.articleId,
    articleNumber = item.articleNumber,
    title = item.title,
    imageUrl = item.imageUrl,
    quantity = item.quantity,
    pricePerPiece = item.pricePerUnit,
    shoppingCartId = item.id,
    quantityPerUnitValue = item.quantityPerUnit.value,
    quantityUnit = item.quantityPerUnit.unit.symbol,
    insertionTime = item.insertionTime,
  )

  fun toItem() =
    Item(
      id = id,
      articleId = articleId,
      articleNumber = articleNumber,
      title = title,
      imageUrl = imageUrl,
      quantity = quantity,
      pricePerUnit = pricePerPiece,
      quantityPerUnit =
        Article.Quantity(quantityPerUnitValue, Article.QuantityUnit.forSymbol(quantityUnit)),
      insertionTime = insertionTime,
    )
}
