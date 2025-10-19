package org.approvej.workshop.service.adapters.demanding.http.shoppingcart

import java.util.*
import org.approvej.workshop.service.application.shoppingcart.ShoppingCartManager
import org.approvej.workshop.service.application.shoppingcart.ShoppingCartNotFoundException
import org.approvej.workshop.service.application.shoppingcart.UnknownArticleException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.internalServerError
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("shopping-cart")
class ShoppingCartController(private val shoppingCartManager: ShoppingCartManager) {

  @GetMapping
  fun getNewShoppingCart(): ResponseEntity<ShoppingCartDto> =
    ok(ShoppingCartDto(shoppingCartManager.createShoppingCart()))

  @GetMapping("{shoppingCartId}")
  fun getShoppingCart(@PathVariable shoppingCartId: String): ResponseEntity<ShoppingCartDto> =
    shoppingCartManager.getShoppingCart(UUID.fromString(shoppingCartId))?.let {
      ok(ShoppingCartDto(it))
    } ?: notFound().build()

  @PostMapping("{shoppingCartId}/items", consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun addItem(
    @PathVariable shoppingCartId: String,
    @RequestBody addItemDto: AddItemDto,
  ): ResponseEntity<ShoppingCartDto> =
    ok(
      ShoppingCartDto(
        shoppingCartManager
          .updateItem(
            UUID.fromString(shoppingCartId),
            UUID.fromString(addItemDto.articleId),
            addItemDto.quantity,
          )
          .getOrThrow()
      )
    )

  @ExceptionHandler
  fun handleException(exception: Exception): ResponseEntity<String> =
    when (exception) {
      is UnknownArticleException -> badRequest().body("Unknown article ${exception.articleId}")
      is ShoppingCartNotFoundException -> notFound().build()
      else -> internalServerError().body(exception.message)
    }
}
