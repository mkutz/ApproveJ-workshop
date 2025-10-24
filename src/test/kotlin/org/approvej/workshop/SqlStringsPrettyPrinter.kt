package org.approvej.workshop

import kotlin.collections.joinToString
import kotlin.text.lines
import kotlin.text.trimEnd
import kotlin.text.trimIndent
import org.approvej.print.Printer
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl

class SqlStringsPrettyPrinter : Printer<List<String>> {

  private val formatter = BasicFormatterImpl()

  companion object {
    fun sqlStringPrettyPrinter() = SqlStringsPrettyPrinter()
  }

  override fun apply(sqlStrings: List<String>): String =
    sqlStrings
      .joinToString(";\n\n") { formatter.format(it).trimIndent() }
      .lines()
      .joinToString("\n") { it.trimEnd() }

  override fun filenameExtension() = "sql"
}
