package org.approvej.workshop.service.application.article

import org.approvej.scrub.Replacements.numbered
import org.approvej.scrub.Scrubbers.stringsMatching

fun articleNumbers() =
  stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}").replacement(numbered("articleNumber"))
