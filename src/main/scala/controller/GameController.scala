package controller

import model.*

class GameController(playerName1: String, playerName2: String):

  private var gameState: GameState =
    GameState(
      players = List(
        Player(playerName1, Hand(Nil)),
        Player(playerName2, Hand(Nil))
      ),
      currentPlayerIndex = 0,
      deck = Deck.shuffled(Cards.allCards)
    )

  def currentPlayer: Player =
    gameState.currentPlayer

  def drawCard(): Option[Card] =
    if gameState.deck.isEmpty then
      None
    else
      gameState = gameState.drawCardForCurrentPlayer
      Some(gameState.currentPlayer.hand.cards.last)

  def switchPlayer(): Unit =
    gameState = gameState.switchPlayer

  def stopGame(): Unit =
    gameState = gameState.stop

  def isRunning: Boolean =
    gameState.running

  def deckSize: Int =
    gameState.deck.size

  def currentPlayerPoints: Int =
    gameState.currentPlayer.basePoints

  def currentPlayerHand: Hand =
    gameState.currentPlayer.hand