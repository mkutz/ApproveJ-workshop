package org.approvej.workshop.service.adapters.providing.database.article

import java.util.UUID.randomUUID
import org.approvej.workshop.TestcontainersConfiguration
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.approvej.workshop.service.application.article.ToStoreArticles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleStoreTest(@param:Autowired private val articleStore: ToStoreArticles) {

  @Test
  fun storeArticle() {
    val article = anArticle().build()

    val storedArticle = articleStore.storeArticle(article)

    assertThat(storedArticle).isEqualTo(article)
    assertThat(articleStore.getArticleById(article.id)).isNotNull()
  }

  @Test
  fun getArticleById() {
    val storedArticleEntity = articleStore.storeArticle(anArticle().build())

    val gottenArticle = articleStore.getArticleById(storedArticleEntity.id)

    assertThat(gottenArticle).isNotNull()
  }

  @Test
  fun getArticleById_unknown() {
    assertThat(articleStore.getArticleById(randomUUID())).isNull()
  }

  @Test
  fun findArticles() {
    val commonTitle = "common title"
    val storedArticlesWithCommonTitle =
      listOf(
        articleStore.storeArticle(anArticle().title("Super $commonTitle").build()),
        articleStore.storeArticle(anArticle().title("Mega $commonTitle").build()),
      )
    val otherStoredArticle = articleStore.storeArticle(anArticle().title("Other title").build())

    val foundArticle = articleStore.findArticles(commonTitle)

    assertThat(foundArticle)
      .containsExactlyInAnyOrderElementsOf(storedArticlesWithCommonTitle)
      .doesNotContain(otherStoredArticle)
  }

  @Test
  fun deleteArticle() {
    val storedArticle = articleStore.storeArticle(anArticle().build())

    articleStore.deleteArticle(storedArticle.id)

    assertThat(articleStore.getArticleById(storedArticle.id)).isNull()
  }
}
