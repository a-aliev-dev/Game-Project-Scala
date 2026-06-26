package controller

import model.interfaces.*
import model.state.*
import util.Observable

trait ControllerInterface extends Observable:

  def getGameState: IGameState

  def setGameState(state: IGameState): Unit

  def dealStartingCards(amount: Int): Unit

  def currentPlayer: IPlayer

  def currentPhase: TurnPhase

  def drawCard(): Option[ICard]

  def drawCardDirectly(): Option[ICard]

  def drawFromDiscardPile(index: Int): Option[ICard]

  def discardCardFromCurrentPlayer(index: Int): Option[ICard]

  def undo(): Unit

  def redo(): Unit

  def stopGame(): Unit

  def isRunning: Boolean

  def deckSize: Int

  def discardPile: List[ICard]

  def discardPileSize: Int

  def currentPlayerPoints: Int

  def playerPoints(playerIndex: Int): Int

  def allPlayerPoints: List[(String, Int)]

  def winnerName: Option[String]

  def currentPlayerHand: IHand
