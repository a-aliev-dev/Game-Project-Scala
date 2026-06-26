package model

import model.interfaces.{ICard, IDeck, IGameState, IPlayer}
import model.state.*

case class GameState(
    players: List[Player],
    currentPlayerIndex: Int,
    deck: Deck,
    discardPile: List[Card] = Nil,
    status: GameStatus = Running,
    turnPhase: TurnPhase = MustDraw
) extends IGameState:

  private val MaxDiscardPileSize = 10

  def currentPlayer: Player =
    players(currentPlayerIndex)

  def drawCardForCurrentPlayer: GameState =
    drawCardForPlayer(currentPlayerIndex)

  def drawCardForPlayer(playerIndex: Int): GameState =
    val (drawnCard, newDeck) = deck.draw

    drawnCard match
      case Some(card: Card) =>
        val player = players(playerIndex)
        val updatedPlayer = player.addCard(card).asInstanceOf[Player]
        val updatedPlayers = players.updated(playerIndex, updatedPlayer)

        copy(
          players = updatedPlayers,
          deck = newDeck.asInstanceOf[Deck],
          turnPhase = MustDiscard
        )

      case _ =>
        this

  def drawFromDiscardPileForCurrentPlayer(index: Int): GameState =
    if index < 0 || index >= discardPile.size then
      this
    else
      val card = discardPile(index)
      val updatedDiscardPile = discardPile.patch(index, Nil, 1)
      val updatedPlayer = currentPlayer.addCard(card).asInstanceOf[Player]
      val updatedPlayers = players.updated(currentPlayerIndex, updatedPlayer)

      copy(
        players = updatedPlayers,
        discardPile = updatedDiscardPile,
        turnPhase = MustDiscard
      )

  def discardCardFromCurrentPlayer(index: Int): GameState =
    val (removedCard, updatedPlayer) = currentPlayer.removeCardAt(index)

    removedCard match
      case Some(card: Card) =>
        val updatedPlayers = players.updated(currentPlayerIndex, updatedPlayer.asInstanceOf[Player])
        val updatedDiscardPile = discardPile :+ card
        val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
        val nextStatus =
          if updatedDiscardPile.size >= MaxDiscardPileSize then Finished
          else status

        copy(
          players = updatedPlayers,
          discardPile = updatedDiscardPile,
          currentPlayerIndex = nextPlayerIndex,
          status = nextStatus,
          turnPhase = MustDraw
        )

      case _ =>
        this

  def dealStartingCards(amount: Int): GameState =
    val dealtState =
      (1 to amount).foldLeft(this) { (state, _) =>
        state.players.indices.foldLeft(state) { (currentState, playerIndex) =>
          currentState.drawCardForPlayer(playerIndex)
        }
      }

    dealtState.copy(turnPhase = MustDraw)

  def switchPlayer: GameState =
    val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
    copy(currentPlayerIndex = nextPlayerIndex, turnPhase = MustDraw)

  def stop: GameState =
    copy(status = Finished)

  def running: Boolean =
    status == Running
