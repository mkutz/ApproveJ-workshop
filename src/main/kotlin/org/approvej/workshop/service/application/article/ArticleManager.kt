package org.approvej.workshop.service.application.article

import java.util.UUID
import org.springframework.stereotype.Component

@Component
class ArticleManager(private val articleStore: ToStoreArticles) : ToManagerArticles {

  override fun importArticle(article: Article) {
    articleStore.storeArticle(article)
  }

  override fun findArticles(query: String) = articleStore.findArticles(query)

  override fun deleteArticle(articleId: UUID) {
    articleStore.deleteArticle(articleId)
  }
}
