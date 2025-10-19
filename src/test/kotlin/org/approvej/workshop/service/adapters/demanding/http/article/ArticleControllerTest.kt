package org.approvej.workshop.service.adapters.demanding.http.article

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse
import java.util.UUID
import org.approvej.workshop.TestcontainersConfiguration
import org.approvej.workshop.service.adapters.demanding.http.toCents
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.approvej.workshop.service.application.article.ToStoreArticles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleControllerTest(
  @param:Value($$"http://localhost:${local.server.port}") val baseUrl: String,
  @param:Autowired val articleStore: ToStoreArticles,
  @param:Autowired val objectMapper: ObjectMapper,
) {

  val httpClient: HttpClient = HttpClient.newHttpClient()

  @Test
  fun get_articles() {
    val query = "ACME"
    val matchingArticles =
      listOf(
        anArticle()
          .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
          .title("$query Rocket")
          .build(),
        anArticle()
          .id(UUID.fromString("00000000-0000-0000-0000-000000000002"))
          .title("$query Slingshot")
          .build(),
        anArticle()
          .id(UUID.fromString("00000000-0000-0000-0000-000000000003"))
          .title("$query Dynamite")
          .build(),
      )
    val notMatchingArticles =
      listOf(
        anArticle().title("OCME Parachute").build(),
        anArticle().title("OCME Trampoline").build(),
      )
    (matchingArticles + notMatchingArticles).forEach { articleStore.storeArticle(it) }

    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/articles?q=$query")).GET().build(),
        HttpResponse.BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(200)
    val receivedArticles = objectMapper.readValue<List<ArticleDto>>(response.body())
    assertThat(receivedArticles).hasSameSizeAs(matchingArticles)
    assertThat(receivedArticles[0].id).isEqualTo(matchingArticles[0].id.toString())
    assertThat(receivedArticles[0].title).isEqualTo(matchingArticles[0].title)
    assertThat(receivedArticles[0].description).isEqualTo(matchingArticles[0].description)
    assertThat(receivedArticles[0].imageUrl).isEqualTo(matchingArticles[0].imageUrl)
    assertThat(receivedArticles[0].pricePerUnit)
      .isEqualTo(matchingArticles[0].pricePerUnit.toCents())
    assertThat(receivedArticles[1].id).isEqualTo(matchingArticles[1].id.toString())
    assertThat(receivedArticles[1].title).isEqualTo(matchingArticles[1].title)
    assertThat(receivedArticles[1].description).isEqualTo(matchingArticles[1].description)
    assertThat(receivedArticles[1].imageUrl).isEqualTo(matchingArticles[1].imageUrl)
    assertThat(receivedArticles[1].pricePerUnit)
      .isEqualTo(matchingArticles[1].pricePerUnit.toCents())
    assertThat(receivedArticles[2].id).isEqualTo(matchingArticles[2].id.toString())
    assertThat(receivedArticles[2].title).isEqualTo(matchingArticles[2].title)
    assertThat(receivedArticles[2].description).isEqualTo(matchingArticles[2].description)
    assertThat(receivedArticles[2].imageUrl).isEqualTo(matchingArticles[2].imageUrl)
    assertThat(receivedArticles[2].pricePerUnit)
      .isEqualTo(matchingArticles[2].pricePerUnit.toCents())
  }
}
