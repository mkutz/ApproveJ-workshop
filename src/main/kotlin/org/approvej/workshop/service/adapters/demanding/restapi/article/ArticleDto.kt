package org.approvej.workshop.service.adapters.demanding.restapi.article

import org.approvej.workshop.service.adapters.demanding.restapi.toCents
import org.approvej.workshop.service.application.article.Article

data class ArticleDto(
  val id: String,
  val articleNumber: String,
  val title: String,
  val description: String,
  val imageUrl: String,
  val pricePerUnit: Int,
  val pricePerUnitFormatted: String,
) {

  constructor(
    article: Article
  ) : this(
    id = article.id.toString(),
    articleNumber = article.articleNumber,
    title = article.title,
    description = article.description,
    imageUrl = article.imageUrl,
    pricePerUnit = article.pricePerUnit.toCents(),
    pricePerUnitFormatted = "${article.pricePerUnit} EUR",
  )
}
