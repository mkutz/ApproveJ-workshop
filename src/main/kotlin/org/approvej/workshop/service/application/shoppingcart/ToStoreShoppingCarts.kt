package org.approvej.workshop.service.application.shoppingcart

import java.util.*

interface ToStoreShoppingCarts {

  fun storeShoppingCart(shoppingCart: ShoppingCart): ShoppingCart

  fun getShoppingCart(shoppingCartId: UUID): ShoppingCart?

  fun addItem(shoppingCartId: UUID, item: Item): Result<ShoppingCart>
}
