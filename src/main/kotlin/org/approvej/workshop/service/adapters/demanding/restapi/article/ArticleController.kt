package org.approvej.workshop.service.adapters.demanding.restapi.article

import org.approvej.workshop.service.application.article.ToStoreArticles
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("articles")
class ArticleController(private val articleQuery: ToStoreArticles) {

  @GetMapping
  fun findArticles(@RequestParam(name = "q") query: String): List<ArticleDto> {
    return articleQuery.findArticles(query).map { ArticleDto(it) }
  }
}
