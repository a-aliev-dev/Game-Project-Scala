package view.render

abstract class Renderer:

  final def render(): String =
    s"""$title
       |${separator}
       |${content}
       |""".stripMargin

  protected def title: String

  protected def content: String

  private def separator: String =
    "-" * title.length