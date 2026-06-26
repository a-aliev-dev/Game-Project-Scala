package model.interfaces

import model.state.{GameStatus, TurnPhase}

trait IGameState:
  def players: List[IPlayer]
  def currentPlayerIndex: Int
  def deck: IDeck
  def discardPile: List[ICard]
  def status: GameStatus
  def turnPhase: TurnPhase
  def currentPlayer: IPlayer
  def drawCardForCurrentPlayer: IGameState
  def drawCardForPlayer(playerIndex: Int): IGameState
  def drawFromDiscardPileForCurrentPlayer(index: Int): IGameState
  def discardCardFromCurrentPlayer(index: Int): IGameState
  def dealStartingCards(amount: Int): IGameState
  def switchPlayer: IGameState
  def stop: IGameState
  def running: Boolean
