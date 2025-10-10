package org.approvej.workshop.service.application.article

import java.util.*

interface ToStoreArticles {

  fun storeArticle(article: Article): Article

  fun getArticleById(articleId: UUID): Article?

  fun findArticles(query: String): List<Article>

  fun deleteArticle(articleId: UUID)
}
