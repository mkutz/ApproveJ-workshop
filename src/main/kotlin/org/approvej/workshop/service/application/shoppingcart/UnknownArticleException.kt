package org.approvej.workshop.service.application.shoppingcart

import java.util.*

class UnknownArticleException(val articleId: UUID) : RuntimeException("Unknown article $articleId")
