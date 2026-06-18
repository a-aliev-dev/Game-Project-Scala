package model

object GameStateFactory:

  def createDefaultGameState(playerName1: String, playerName2: String): GameState =
    GameState(
      players = List(
        Player(playerName1, Hand(Nil)),
        Player(playerName2, Hand(Nil))
      ),
      currentPlayerIndex = 0,
      deck = Deck.shuffled(Cards.allCards)
    )

  def createTestGameState(
      playerName1: String,
      playerName2: String,
      deck: Deck
  ): GameState =
    GameState(
      players = List(
        Player(playerName1, Hand(Nil)),
        Player(playerName2, Hand(Nil))
      ),
      currentPlayerIndex = 0,
      deck = deck
    )