package org.approvej.workshop.service.adapters.demanding.http.article

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse
import java.util.UUID
import org.approvej.ApprovalBuilder.approve
import org.approvej.json.jackson.JsonPrintFormat.json
import org.approvej.scrub.Scrubbers.stringsMatching
import org.approvej.workshop.TestcontainersConfiguration
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
) {

  val httpClient: HttpClient = HttpClient.newHttpClient()

  @Test
  fun `get articles`() {
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

    approve(response.body())
      .printedAs(json())
      .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
      .byFile()
  }
}
