package org.approvej.workshop.service.adapters.providing.database.article

import org.approvej.print.PrintFormat
import org.approvej.print.Printer
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl

class SqlStringsPrettyPrinter : PrintFormat<List<String>> {

  private val formatter = BasicFormatterImpl()

  companion object {
    fun sqlStringPrettyPrinter() = SqlStringsPrettyPrinter()
  }

  override fun printer(): Printer<List<String>> {
    return Printer { sqlStrings: List<String> ->
      sqlStrings
        .map { formatter.format(it).trimIndent() }
        .joinToString(";\n\n")
        .lines()
        .map { it.trimEnd() }
        .joinToString("\n")
    }
  }

  override fun filenameExtension() = "sql"
}
