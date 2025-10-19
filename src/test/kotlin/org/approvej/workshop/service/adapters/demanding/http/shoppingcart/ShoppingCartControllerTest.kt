package org.approvej.workshop.service.adapters.demanding.http.shoppingcart

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers
import java.util.UUID.randomUUID
import org.approvej.workshop.TestcontainersConfiguration
import org.approvej.workshop.service.adapters.demanding.http.toCents
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
  @param:Autowired val objectMapper: ObjectMapper,
) {

  val httpClient: HttpClient = HttpClient.newHttpClient()

  @Test
  fun get_new_shopping_cart() {
    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart")).GET().build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(200)
    val receivedShoppingCart = objectMapper.readValue<ShoppingCartDto>(response.body())
    assertThat(receivedShoppingCart).isNotNull()
    assertThat(receivedShoppingCart.id).isNotNull()
    assertThat(receivedShoppingCart.value).isEqualTo(0)
    assertThat(receivedShoppingCart.items).isEmpty()
  }

  @Test
  fun get_existing_shopping_cart() {
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
    val receivedShoppingCart = objectMapper.readValue<ShoppingCartDto>(response.body())
    assertThat(receivedShoppingCart).isNotNull()
    assertThat(receivedShoppingCart.id).isEqualTo(existingShoppingCart.id.toString())
    assertThat(receivedShoppingCart.value).isPositive()
    val receivedItems = receivedShoppingCart.items
    assertThat(receivedItems).hasSize(2)
    assertThat(receivedItems[0].id).isEqualTo(existingShoppingCart.items[0].id.toString())
    assertThat(receivedItems[0].articleId)
      .isEqualTo(existingShoppingCart.items[0].articleId.toString())
    assertThat(receivedItems[0].title).isEqualTo(existingShoppingCart.items[0].title)
    assertThat(receivedItems[0].imageUrl).isEqualTo(existingShoppingCart.items[0].imageUrl)
    assertThat(receivedItems[0].quantity).isEqualTo(existingShoppingCart.items[0].quantity)
    assertThat(receivedItems[0].pricePerUnit)
      .isEqualTo(existingShoppingCart.items[0].pricePerUnit.toCents())
    assertThat(receivedItems[0].priceTotal)
      .isEqualTo(existingShoppingCart.items[0].priceTotal.toCents())
    assertThat(receivedItems[1].id).isEqualTo(existingShoppingCart.items[1].id.toString())
    assertThat(receivedItems[1].articleId)
      .isEqualTo(existingShoppingCart.items[1].articleId.toString())
    assertThat(receivedItems[1].title).isEqualTo(existingShoppingCart.items[1].title)
    assertThat(receivedItems[1].imageUrl).isEqualTo(existingShoppingCart.items[1].imageUrl)
    assertThat(receivedItems[1].quantity).isEqualTo(existingShoppingCart.items[1].quantity)
    assertThat(receivedItems[1].pricePerUnit)
      .isEqualTo(existingShoppingCart.items[1].pricePerUnit.toCents())
    assertThat(receivedItems[1].priceTotal)
      .isEqualTo(existingShoppingCart.items[1].priceTotal.toCents())
  }

  @Test
  fun get_existing_shopping_cart_unknown_id() {
    val response =
      httpClient.send(
        newBuilder(URI("$baseUrl/shopping-cart/${randomUUID()}")).GET().build(),
        BodyHandlers.ofString(),
      )

    assertThat(response.statusCode()).isEqualTo(404)
  }

  @Test
  fun post_shopping_cart_items() {
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
    val receivedShoppingCart = objectMapper.readValue<ShoppingCartDto>(response.body())
    assertThat(receivedShoppingCart).isNotNull()
    assertThat(receivedShoppingCart.id).isEqualTo(existingShoppingCart.id.toString())
    assertThat(receivedShoppingCart.value).isEqualTo(article.pricePerUnit.toCents() * quantity)
    assertThat(receivedShoppingCart.items).hasSize(1)
    assertThat(receivedShoppingCart.items[0].id).isNotNull()
    assertThat(receivedShoppingCart.items[0].articleId).isEqualTo(article.id.toString())
    assertThat(receivedShoppingCart.items[0].title).isEqualTo(article.title)
    assertThat(receivedShoppingCart.items[0].imageUrl).isEqualTo(article.imageUrl)
    assertThat(receivedShoppingCart.items[0].quantity).isEqualTo(quantity)
    assertThat(receivedShoppingCart.items[0].pricePerUnit).isEqualTo(article.pricePerUnit.toCents())
    assertThat(receivedShoppingCart.items[0].priceTotal)
      .isEqualTo(article.pricePerUnit.toCents() * quantity)
  }

  @Test
  fun post_shopping_cart_items_unknown_shopping_cart() {
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
  fun post_shopping_cart_items_unknown_article() {
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
