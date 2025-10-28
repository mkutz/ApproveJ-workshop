package org.approvej.workshop.service.adapters.demanding.http.shoppingcart

import org.approvej.workshop.service.adapters.demanding.http.toCents
import org.approvej.workshop.service.application.shoppingcart.ItemBuilder.Companion.anItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ItemDtoTest {

  @Test
  fun `constructor item`() {
    val item = anItem().build()

    val dto = ItemDto(item)

    assertThat(dto.id).isEqualTo(item.id.toString())
    assertThat(dto.articleId).isEqualTo(item.articleId.toString())
    assertThat(dto.title).isEqualTo(item.title)
    assertThat(dto.imageUrl).isEqualTo(item.imageUrl)
    assertThat(dto.quantity).isEqualTo(item.quantity)
    assertThat(dto.pricePerUnit).isEqualTo(item.pricePerUnit.toCents())
    assertThat(dto.priceTotal).isEqualTo(item.priceTotal.toCents())
    assertThat(dto.quantityPerUnitValue).isEqualTo(item.quantityPerUnit.value)
    assertThat(dto.quantityTotalValue).isEqualTo(item.quantityTotal.value)
    assertThat(dto.quantityUnitSymbol).isEqualTo(item.quantityPerUnit.unit.symbol)
    assertThat(dto.insertionTime).isEqualTo(item.insertionTime)
  }
}
