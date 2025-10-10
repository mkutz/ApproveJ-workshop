package org.approvej.workshop.service.application.article

import java.math.BigDecimal
import java.util.*
import kotlin.text.get

data class Article(
  val id: UUID,
  val articleNumber: String,
  val title: String,
  val description: String,
  val imageUrl: String,
  val pricePerUnit: BigDecimal,
  val quantityPerUnit: Quantity,
) {

  data class Quantity(val value: BigDecimal, val unit: QuantityUnit) {}

  enum class QuantityUnit(val symbol: String) {

    GRAM("g"),
    KILOGRAM("kg"),
    MILLILITER("ml"),
    LITER("l"),
    CENTIMETER("cm"),
    METER("m"),
    PIECE("");

    companion object {
      fun forSymbol(symbol: String?): QuantityUnit =
        entries.find { it.symbol.equals(symbol, true) } ?: PIECE
    }
  }
}
