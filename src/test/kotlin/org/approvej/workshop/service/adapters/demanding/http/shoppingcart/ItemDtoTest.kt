package org.approvej.workshop.service.adapters.demanding.http.shoppingcart

import org.approvej.ApprovalBuilder.approve
import org.approvej.json.jackson.JsonPrintFormat.json
import org.approvej.scrub.Scrubbers.isoInstants
import org.approvej.scrub.Scrubbers.stringsMatching
import org.approvej.scrub.Scrubbers.uuids
import org.approvej.workshop.service.application.shoppingcart.ItemBuilder.Companion.anItem
import org.junit.jupiter.api.Test

class ItemDtoTest {

  @Test
  fun `constructor item`() {
    val item = anItem().build()

    val dto = ItemDto(item)

    approve(dto)
      .printedAs(json())
      .scrubbedOf(uuids())
      .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
      .scrubbedOf(isoInstants())
      .byFile()
  }
}
