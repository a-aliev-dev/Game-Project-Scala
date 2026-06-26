package controller

trait UndoManagerInterface:

  def doStep(command: Command): Unit

  def undoStep(): Unit

  def redoStep(): Unit
