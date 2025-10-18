package org.approvej.workshop.service.adapters.demanding.kafka.article

import java.lang.RuntimeException

class InvalidQuantityStringException(invalidQuantityString: String) :
  RuntimeException("Invalid quantity string: $invalidQuantityString")
