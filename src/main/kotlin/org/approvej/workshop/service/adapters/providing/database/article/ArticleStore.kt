package org.approvej.workshop.service.adapters.providing.database.article

import java.util.*
import org.approvej.workshop.service.application.article.Article
import org.approvej.workshop.service.application.article.ToStoreArticles
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ArticleStore(private val articleRepository: ArticleRepository) : ToStoreArticles {

  override fun getArticleById(articleId: UUID) =
    articleRepository.findByIdOrNull(articleId)?.toArticle()

  override fun findArticles(query: String) =
    articleRepository.findAllByTitleContainsIgnoreCase(query).map { it.toArticle() }

  override fun storeArticle(article: Article): Article =
    articleRepository.save(ArticleEntity(article)).toArticle()

  override fun deleteArticle(articleId: UUID) {
    articleRepository.deleteById(articleId)
  }
}
