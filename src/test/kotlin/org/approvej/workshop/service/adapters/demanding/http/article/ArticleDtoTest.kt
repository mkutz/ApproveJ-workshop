package org.approvej.workshop.service.adapters.demanding.http.article

import org.approvej.ApprovalBuilder.approve
import org.approvej.print.MultiLineStringPrintFormat.multiLineString
import org.approvej.scrub.Scrubbers.uuids
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.approvej.workshop.service.application.article.articleNumbers
import org.junit.jupiter.api.Test

class ArticleDtoTest {

  @Test
  fun `constructor article`() {
    val article = anArticle().build()

    val dto = ArticleDto(article)

    approve(dto)
      .printedAs(multiLineString())
      .scrubbedOf(uuids())
      .scrubbedOf(articleNumbers())
      .byFile()
  }
}
