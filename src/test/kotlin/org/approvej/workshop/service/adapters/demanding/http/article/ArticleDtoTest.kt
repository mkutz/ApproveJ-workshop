package org.approvej.workshop.service.adapters.demanding.http.article

import org.approvej.ApprovalBuilder.approve
import org.approvej.json.jackson.JsonPrintFormat.json
import org.approvej.scrub.Scrubbers.stringsMatching
import org.approvej.scrub.Scrubbers.uuids
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.junit.jupiter.api.Test

class ArticleDtoTest {

  @Test
  fun `constructor article`() {
    val article = anArticle().build()

    val dto = ArticleDto(article)

    approve(dto)
      .printedAs(json())
      .scrubbedOf(uuids())
      .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
      .byFile()
  }
}
