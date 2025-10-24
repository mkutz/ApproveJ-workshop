package org.approvej.workshop.service.adapters.demanding.http.article

import org.approvej.ApprovalBuilder.approve
import org.approvej.print.ObjectPrinter.objectPrinter
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
      .printWith(objectPrinter())
      .scrubbedOf(uuids())
      .scrubbedOf(articleNumbers())
      .byFile()
  }
}
