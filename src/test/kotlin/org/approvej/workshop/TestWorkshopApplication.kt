package org.approvej.workshop

import org.approvej.workshop.service.ShoppingCartApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
  fromApplication<ShoppingCartApplication>().with(TestcontainersConfiguration::class).run(*args)
}
