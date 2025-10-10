package org.approvej.workshop.service.adapters.providing.database.article

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.*
import org.approvej.workshop.service.application.article.Article

@Entity
@Table(name = "article")
data class ArticleEntity(
  @Id val id: UUID,
  val articleNumber: String,
  val title: String,
  val description: String,
  val imageUrl: String,
  val pricePerUnit: BigDecimal,
  val quantityPerUnitValue: BigDecimal,
  val quantityUnit: String,
) {

  constructor(
    article: Article
  ) : this(
    id = article.id,
    articleNumber = article.articleNumber,
    title = article.title,
    description = article.description,
    imageUrl = article.imageUrl,
    pricePerUnit = article.pricePerUnit,
    quantityPerUnitValue = article.quantityPerUnit.value,
    quantityUnit = article.quantityPerUnit.unit.symbol,
  )

  fun toArticle() =
    Article(
      id = id,
      articleNumber = articleNumber,
      title = title,
      description = description,
      imageUrl = imageUrl,
      pricePerUnit = pricePerUnit,
      quantityPerUnit =
        Article.Quantity(quantityPerUnitValue, Article.QuantityUnit.forSymbol(quantityUnit)),
    )
}
