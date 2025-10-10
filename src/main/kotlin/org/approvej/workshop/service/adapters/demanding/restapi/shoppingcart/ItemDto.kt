package org.approvej.workshop.service.adapters.demanding.restapi.shoppingcart

import java.math.BigDecimal
import java.time.Instant
import org.approvej.workshop.service.adapters.demanding.restapi.toCents
import org.approvej.workshop.service.application.shoppingcart.Item

data class ItemDto(
  val id: String,
  val articleId: String,
  val articleNumber: String,
  val title: String,
  val imageUrl: String,
  val quantity: Int,
  val pricePerUnit: Int,
  val priceTotal: Int,
  val quantityPerUnitValue: BigDecimal,
  val quantityTotalValue: BigDecimal,
  val quantityUnitSymbol: String,
  val insertionTime: Instant,
) {

  constructor(
    item: Item
  ) : this(
    id = item.id.toString(),
    articleId = item.articleId.toString(),
    articleNumber = item.articleNumber,
    title = item.title,
    imageUrl = item.imageUrl,
    quantity = item.quantity,
    pricePerUnit = item.pricePerUnit.toCents(),
    priceTotal = item.priceTotal.toCents(),
    quantityPerUnitValue = item.quantityPerUnit.value,
    quantityTotalValue = item.quantityTotal.value,
    quantityUnitSymbol = item.quantityPerUnit.unit.symbol,
    insertionTime = item.insertionTime,
  )
}
