package org.approvej.workshop.service.application.article

import java.lang.RuntimeException

class InvalidQuantityStringException(invalidQuantityString: String) :
  RuntimeException("Invalid quantity string: $invalidQuantityString")
