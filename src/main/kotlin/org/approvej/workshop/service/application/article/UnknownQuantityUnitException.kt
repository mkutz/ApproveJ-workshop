package org.approvej.workshop.service.application.article

class UnknownQuantityUnitException(val symbol: String) :
  RuntimeException("Unknown quantity unit: $symbol")
