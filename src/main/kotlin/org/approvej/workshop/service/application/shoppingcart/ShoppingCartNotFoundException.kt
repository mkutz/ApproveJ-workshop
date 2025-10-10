package org.approvej.workshop.service.application.shoppingcart

import java.util.UUID

class ShoppingCartNotFoundException(val shoppingCartId: UUID) :
  RuntimeException("Shopping cart $shoppingCartId not found")
