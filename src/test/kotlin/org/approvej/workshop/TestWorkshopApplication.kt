package org.approvej.workshop

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
  fromApplication<WorkshopApplication>().with(TestcontainersConfiguration::class).run(*args)
}
