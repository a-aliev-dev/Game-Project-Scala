package controller

import model.*
import model.score.*
import util.Observable

class GameController(playerName1: String, playerName2: String) extends Observable:

  private var gameState: GameState =
    GameState(
      players = List(
        Player(playerName1, Hand(Nil)),
        Player(playerName2, Hand(Nil))
      ),
      currentPlayerIndex = 0,
      deck = Deck.shuffled(Cards.allCards)
    )

  private val scoreStrategy: ScoreStrategy =
    BaseScoreStrategy

  private val undoManager =
    new UndoManager()

  def getGameState: GameState =
    gameState

  def setGameState(state: GameState): Unit =
    gameState = state
    notifyObservers()

  def currentPlayer: Player =
    gameState.currentPlayer

  def drawCard(): Option[Card] =
    val before = gameState.currentPlayer.hand.cards.size
    undoManager.doStep(new DrawCardCommand(this))
    val after = gameState.currentPlayer.hand.cards.size

    if after > before then
      gameState.currentPlayer.hand.cards.lastOption
    else
      None

  def drawCardDirectly(): Option[Card] =
    if gameState.deck.isEmpty then
      None
    else
      gameState = gameState.drawCardForCurrentPlayer
      notifyObservers()
      gameState.currentPlayer.hand.cards.lastOption

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

  def currentPlayerPoints: Int =
    scoreStrategy.calculate(gameState.currentPlayer.hand)

  def currentPlayerHand: Hand =
    gameState.currentPlayer.hand