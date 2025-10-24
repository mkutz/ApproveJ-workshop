package org.approvej.workshop.service.adapters.providing.database.shoppingcart

import java.util.UUID.randomUUID
import org.approvej.ApprovalBuilder.approve
import org.approvej.workshop.SqlStringsPrettyPrinter.Companion.sqlStringPrettyPrinter
import org.approvej.workshop.TestcontainersConfiguration
import org.approvej.workshop.service.application.shoppingcart.ItemBuilder.Companion.anItem
import org.approvej.workshop.service.application.shoppingcart.ShoppingCartBuilder.Companion.aShoppingCart
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartStoreTest(
  @param:Autowired private val shoppingCartStore: ShoppingCartStore,
  @param:Autowired private val queryCollector: TestcontainersConfiguration.QueryCollector,
) {

  @BeforeEach
  fun resetCollectedQueries() {
    queryCollector.reset()
  }

  @Test
  fun storeShoppingCart() {
    val shoppingCart = aShoppingCart().build()

    val storedShoppingCart = shoppingCartStore.storeShoppingCart(shoppingCart)

    assertThat(storedShoppingCart).isEqualTo(shoppingCart)
    assertThat(shoppingCartStore.getShoppingCart(shoppingCart.id)).isEqualTo(storedShoppingCart)

    approve(queryCollector.queries().map { it.query }).printWith(sqlStringPrettyPrinter()).byFile()
  }

  @Test
  fun getShoppingCart() {
    val storedShoppingCart = shoppingCartStore.storeShoppingCart(aShoppingCart().build())

    val gottenShoppingCart = shoppingCartStore.getShoppingCart(storedShoppingCart.id)

    assertThat(gottenShoppingCart).isEqualTo(storedShoppingCart)
  }

  @Test
  fun getShoppingCart_unknown() {
    assertThat(shoppingCartStore.getShoppingCart(randomUUID())).isNull()
  }

  @Test
  fun addItem() {
    val storedShoppingCart = shoppingCartStore.storeShoppingCart(aShoppingCart().build())
    val item = anItem().build()

    val result = shoppingCartStore.addItem(storedShoppingCart.id, item)

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrThrow().items.single()).isEqualTo(item)
  }
}
