package org.approvej.workshop.service.application.article

import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArticleManagerTest {

  private val articleStore = ArticleStoreStub()
  private val articleManager = ArticleManager(articleStore)

  @Test
  fun importArticle() {
    val article = anArticle().build()

    articleManager.importArticle(article)

    assertThat(articleStore.getArticleById(article.id)).isEqualTo(article)
  }

  @Test
  fun findArticles() {
    val query = "ACME"
    val matchingArticles =
      listOf(
        anArticle().title("$query Rocket").build(),
        anArticle().title("$query Slingshot").build(),
        anArticle().title("$query Dynamite").build(),
      )
    val notMatchingArticles =
      listOf(
        anArticle().title("OCME Parachute").build(),
        anArticle().title("OCME Trampoline").build(),
      )
    (matchingArticles + notMatchingArticles).forEach { articleStore.storeArticle(it) }

    val foundArticles = articleManager.findArticles(query)

    assertThat(foundArticles).isEqualTo(matchingArticles)
  }
}
