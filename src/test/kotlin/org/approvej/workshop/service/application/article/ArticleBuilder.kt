package org.approvej.workshop.service.application.article

import java.math.BigDecimal
import java.util.*
import java.util.UUID.randomUUID
import kotlin.apply
import org.stubit.random.RandomString.aStringStartingWith
import org.stubit.random.RandomString.arabicDigits
import org.stubit.random.RandomString.latinLetters

class ArticleBuilder private constructor() {
  var id: UUID = randomUUID()
  var articleNumber: String =
    aStringStartingWith("A-")
      .followedBy(latinLetters(3).uppercase())
      .followedBy("-")
      .followedBy(arabicDigits(3))
      .followedBy("-")
      .followedBy(arabicDigits(3))
      .build()
  var title: String = "Some title"
  var description: String = "Some description"
  var price: BigDecimal = BigDecimal("2.99")
  var quantityPerUnit = Article.Quantity(BigDecimal.valueOf(100), Article.QuantityUnit.GRAM)

  companion object {
    fun anArticle(): ArticleBuilder = ArticleBuilder()
  }

  fun id(id: UUID) = apply { this.id = id }

  fun articleNumber(articleNumber: String) = apply { this.articleNumber = articleNumber }

  fun title(title: String) = apply { this.title = title }

  fun description(description: String) = apply { this.description = description }

  fun price(price: BigDecimal) = apply { this.price = price }

  fun price(price: String) = apply { this.price = BigDecimal(price) }

  fun quantityPerUnit(unit: Article.Quantity) = apply { this.quantityPerUnit = unit }

  fun build() =
    Article(
      id = id,
      articleNumber = articleNumber,
      title = title,
      description = description,
      imageUrl = "https://approvej.org/images/$id",
      pricePerUnit = price,
      quantityPerUnit = quantityPerUnit,
    )
}
