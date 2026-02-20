package org.approvej.workshop.service.adapters.demanding.http.shoppingcart

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers
import java.util.UUID.randomUUID
import org.approvej.ApprovalBuilder.approve
import org.approvej.json.jackson.JsonPrintFormat.json
import org.approvej.scrub.Scrubbers.isoInstants
import org.approvej.scrub.Scrubbers.stringsMatching
import org.approvej.scrub.Scrubbers.uuids
import org.approvej.workshop.TestcontainersConfiguration
import org.approvej.workshop.service.application.article.ArticleBuilder.Companion.anArticle
import org.approvej.workshop.service.application.article.ToStoreArticles
import org.approvej.workshop.service.application.shoppingcart.ItemBuilder.Companion.anItem
import org.approvej.workshop.service.application.shoppingcart.ShoppingCartBuilder.Companion.aShoppingCart
import org.approvej.workshop.service.application.shoppingcart.ToStoreShoppingCarts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest(
  @param:Value($$"http://localhost:${local.server.port}") val baseUrl: String,
  @param:Autowired val shoppingCartStore: ToStoreShoppingCarts,
  @param:Autowired val articleStore: ToStoreArticles,
) {

  val httpClient: HttpClient = HttpClient.newHttpClient()

  @Test
  fun `get new shopping cart`() {
    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart")).GET().build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(200)

    approve(response.body())
      .printedAs(json())
      .scrubbedOf(uuids())
      .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
      .scrubbedOf(isoInstants())
      .byFile()
  }

  @Test
  fun `get existing shopping cart`() {
    val existingShoppingCart =
      shoppingCartStore.storeShoppingCart(
        aShoppingCart().items(anItem().build(), anItem().build()).build()
      )

    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart/${existingShoppingCart.id}")).GET().build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(200)

    approve(response.body())
      .printedAs(json())
      .scrubbedOf(uuids())
      .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
      .scrubbedOf(isoInstants())
      .byFile()
  }

  @Test
  fun `get existing shopping cart unknown id`() {
    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart/${randomUUID()}")).GET().build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(404)
  }

  @Test
  fun `post shopping cart items`() {
    val existingShoppingCart = shoppingCartStore.storeShoppingCart(aShoppingCart().build())
    val article = articleStore.storeArticle(anArticle().build())
    val quantity = 2

    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart/${existingShoppingCart.id}/items"))
          .POST(
            BodyPublishers.ofString("""{"articleId":"${article.id}","quantity": ${quantity}}""")
          )
          .header("Content-Type", "application/json")
          .build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(200)

    approve(response.body())
      .printedAs(json())
      .scrubbedOf(uuids())
      .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
      .scrubbedOf(isoInstants())
      .byFile()
  }

  @Test
  fun `post shopping cart items unknown shopping cart`() {
    val unknownShoppingCartId = randomUUID()
    val article = articleStore.storeArticle(anArticle().build())

    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart/$unknownShoppingCartId/items"))
          .POST(BodyPublishers.ofString("""{"articleId":"${article.id}","quantity": 1}"""))
          .header("Content-Type", "application/json")
          .build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(404)
  }

  @Test
  fun `post shopping cart items unknown article`() {
    val existingShoppingCart = shoppingCartStore.storeShoppingCart(aShoppingCart().build())
    val unknownArticleId = randomUUID()

    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart/${existingShoppingCart.id}/items"))
          .POST(BodyPublishers.ofString("""{"articleId":"${unknownArticleId}","quantity": 1}"""))
          .header("Content-Type", "application/json")
          .build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(400)
    assertThat(response.body()).isEqualTo("Unknown article $unknownArticleId")
  }
}
