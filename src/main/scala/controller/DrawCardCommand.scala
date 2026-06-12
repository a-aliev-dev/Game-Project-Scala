package controller

import model.GameState

class DrawCardCommand(controller: GameController) extends Command:

  private var previousState: Option[GameState] = None
  private var nextState: Option[GameState] = None

  override def doStep(): Unit =
    previousState = Some(controller.getGameState)
    controller.drawCardDirectly()
    nextState = Some(controller.getGameState)

  override def undoStep(): Unit =
    previousState match
      case Some(state) =>
        controller.setGameState(state)

      case None =>
        println("Nothing to undo.")

  override def redoStep(): Unit =
    nextState match
      case Some(state) =>
        controller.setGameState(state)

      case None =>
        println("Nothing to redo.")