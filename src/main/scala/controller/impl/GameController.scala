package controller

import com.google.inject.Inject
import model.*
import model.interfaces.*
import model.score.*
import model.state.*

class GameController @Inject() (
    playerName1: String,
    playerName2: String,
    private val scoreStrategy: ScoreStrategy = FantasyRealmsScoreStrategy,
    private val undoManager: UndoManagerInterface = new UndoManager()
) extends ControllerInterface:

  private var gameState: GameState =
    GameStateFactory.createDefaultGameState(playerName1, playerName2)


  def getGameState: IGameState =
    gameState

  def setGameState(state: IGameState): Unit =
    state match
      case concreteState: GameState =>
        gameState = concreteState
        notifyObservers()
      case _ =>
        throw new IllegalArgumentException("GameController needs a GameState implementation")

  def dealStartingCards(amount: Int): Unit =
    gameState = gameState.dealStartingCards(amount)
    notifyObservers()

  def currentPlayer: IPlayer =
    gameState.currentPlayer

  def currentPhase: TurnPhase =
    gameState.turnPhase

  def drawCard(): Option[ICard] =
    if gameState.turnPhase != MustDraw then
      None
    else
      val before = gameState.currentPlayer.hand.cards.size
      undoManager.doStep(new DrawCardCommand(this))
      val after = gameState.currentPlayer.hand.cards.size

      if after > before then
        gameState.currentPlayer.hand.cards.lastOption
      else
        None

  def drawCardDirectly(): Option[ICard] =
    if gameState.deck.isEmpty || gameState.turnPhase != MustDraw then
      None
    else
      gameState = gameState.drawCardForCurrentPlayer
      notifyObservers()
      gameState.currentPlayer.hand.cards.lastOption

  def drawFromDiscardPile(index: Int): Option[ICard] =
    if gameState.turnPhase != MustDraw then
      None
    else if index < 0 || index >= gameState.discardPile.size then
      None
    else
      val card = gameState.discardPile(index)
      gameState = gameState.drawFromDiscardPileForCurrentPlayer(index)
      notifyObservers()
      Some(card)

  def discardCardFromCurrentPlayer(index: Int): Option[ICard] =
    if gameState.turnPhase != MustDiscard then
      None
    else if index < 0 || index >= gameState.currentPlayer.hand.cards.size then
      None
    else
      val card = gameState.currentPlayer.hand.cards(index)
      gameState = gameState.discardCardFromCurrentPlayer(index)
      notifyObservers()
      Some(card)

  def undo(): Unit =
    undoManager.undoStep()

  def redo(): Unit =
    undoManager.redoStep()

  def switchPlayer(): Unit =
    gameState = gameState.switchPlayer
    notifyObservers()

  def stopGame(): Unit =
    gameState = gameState.stop
    notifyObservers()

  def isRunning: Boolean =
    gameState.running

  def deckSize: Int =
    gameState.deck.size

  def discardPile: List[ICard] =
    gameState.discardPile

  def discardPileSize: Int =
    gameState.discardPile.size

  def currentPlayerPoints: Int =
    scoreStrategy.calculate(gameState.currentPlayer.hand)

  def playerPoints(playerIndex: Int): Int =
    scoreStrategy.calculate(gameState.players(playerIndex).hand)

  def allPlayerPoints: List[(String, Int)] =
    gameState.players.map(player => player.name -> scoreStrategy.calculate(player.hand))

  def winnerName: Option[String] =
    if gameState.running then
      None
    else
      val scores = allPlayerPoints
      val maxScore = scores.map(_._2).max
      val winners = scores.filter(_._2 == maxScore).map(_._1)

      if winners.size == 1 then Some(winners.head)
      else Some(winners.mkString("Unentschieden zwischen ", " und ", ""))

  def currentPlayerHand: IHand =
    gameState.currentPlayer.hand
