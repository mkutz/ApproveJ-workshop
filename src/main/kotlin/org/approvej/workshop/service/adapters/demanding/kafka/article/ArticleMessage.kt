package org.approvej.workshop.service.adapters.demanding.kafka.article

import java.math.BigDecimal
import java.util.UUID
import org.approvej.workshop.service.application.article.Article
import org.approvej.workshop.service.application.article.Article.Quantity

data class ArticleMessage(
  val id: String,
  val articleNumber: String,
  val title: String,
  val description: String,
  val imageUrl: String,
  val pricePerUnit: String,
  val quantityPerUnit: String,
) {

  fun toArticle() =
    Article(
      id = UUID.fromString(id),
      articleNumber = articleNumber,
      title = title,
      description = description,
      imageUrl = imageUrl,
      pricePerUnit = BigDecimal(pricePerUnit),
      quantityPerUnit =
        "^(?<value>\\d+(\\.\\d+)?)\\s*(?<unit>\\w+)?$"
          .toRegex(RegexOption.IGNORE_CASE)
          .find(quantityPerUnit)
          ?.let {
            Quantity(
              BigDecimal(it.groups["value"]?.value),
              Article.QuantityUnit.forSymbol(it.groups["unit"]?.value ?: ""),
            )
          } ?: throw InvalidQuantityStringException(quantityPerUnit),
    )
}
