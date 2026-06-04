package model

import model.state.*

case class GameState(
  players: List[Player],
  currentPlayerIndex: Int,
  deck: Deck,
  status: GameStatus = Running
):

  def currentPlayer: Player =
    players(currentPlayerIndex)

  def drawCardForCurrentPlayer: GameState =
    drawCardForPlayer(currentPlayerIndex)

  def drawCardForPlayer(playerIndex: Int): GameState =
    val (drawnCard, newDeck) = deck.draw

    drawnCard match
      case Some(card) =>
        val player = players(playerIndex)
        val updatedPlayer = player.addCard(card)
        val updatedPlayers = players.updated(playerIndex, updatedPlayer)

        copy(
          players = updatedPlayers,
          deck = newDeck
        )

      case None =>
        this

  def dealStartingCards(amount: Int): GameState =
    (1 to amount).foldLeft(this) { (state, _) =>
      state.players.indices.foldLeft(state) { (currentState, playerIndex) =>
        currentState.drawCardForPlayer(playerIndex)
      }
    }

  def switchPlayer: GameState =
    val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
    copy(currentPlayerIndex = nextPlayerIndex)

  def stop: GameState =
    copy(status = Finished)

  def running: Boolean =
    status == Running