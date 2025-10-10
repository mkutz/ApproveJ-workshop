package org.approvej.workshop.service.application.article

import java.util.UUID

interface ToManagerArticles {

  fun importArticle(article: Article)

  fun findArticles(query: String): List<Article>

  fun deleteArticle(articleId: UUID)
}
