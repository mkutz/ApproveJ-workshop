package org.approvej.workshop.service.adapters.demanding.http.shoppingcart

import org.approvej.workshop.service.adapters.demanding.http.toCents
import org.approvej.workshop.service.application.shoppingcart.ShoppingCart

data class ShoppingCartDto(val id: String, val items: List<ItemDto>, val value: Int) {

  constructor(
    shoppingCart: ShoppingCart
  ) : this(
    id = shoppingCart.id.toString(),
    items = shoppingCart.items.map { ItemDto(it) },
    value = shoppingCart.value.toCents(),
  )
}
