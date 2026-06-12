import model.*
import model.state.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameStateSpec extends AnyWordSpec with Matchers:

  private val PlayerOneName = "Spieler 1"
  private val PlayerTwoName = "Spieler 2"

  private val ranger = Card("Ranger", CardType.Army, 5)
  private val island = Card("Island", CardType.Flood, 14)
  private val fireElemental = Card("Fire Elemental", CardType.Flame, 4)

  private def emptyStateWithDeck(deck: Deck): GameState =
    GameState(
      players = List(
        Player(PlayerOneName, Hand(Nil)),
        Player(PlayerTwoName, Hand(Nil))
      ),
      currentPlayerIndex = 0,
      deck = deck
    )

  "A GameState" should {

    "draw one card from the deck and add it to the current player's hand" in {
      val gameState = emptyStateWithDeck(Deck(List(ranger, island)))

      val updatedState = gameState.drawCardForCurrentPlayer

      updatedState.players(0).hand.cards shouldBe List(ranger)
      updatedState.players(1).hand.cards shouldBe Nil
      updatedState.deck.cards shouldBe List(island)
      updatedState.turnPhase shouldBe MustDiscard
    }

    "not change when drawing from an empty deck" in {
      val gameState = emptyStateWithDeck(Deck(Nil))

      val updatedState = gameState.drawCardForCurrentPlayer

      updatedState shouldBe gameState
    }

    "draw a card for a specific player" in {
      val gameState = emptyStateWithDeck(Deck(List(ranger, island)))

      val updatedState = gameState.drawCardForPlayer(1)

      updatedState.players(0).hand.cards shouldBe Nil
      updatedState.players(1).hand.cards shouldBe List(ranger)
      updatedState.deck.cards shouldBe List(island)
    }

    "deal starting cards to both players" in {
      val gameState = emptyStateWithDeck(Deck(List(ranger, island, fireElemental)))

      val updatedState = gameState.dealStartingCards(1)

      updatedState.players(0).hand.cards shouldBe List(ranger)
      updatedState.players(1).hand.cards shouldBe List(island)
      updatedState.deck.cards shouldBe List(fireElemental)
      updatedState.turnPhase shouldBe MustDraw
    }

    "draw a card from the discard pile" in {
      val gameState =
        emptyStateWithDeck(Deck(Nil)).copy(discardPile = List(ranger, island))

      val updatedState = gameState.drawFromDiscardPileForCurrentPlayer(1)

      updatedState.currentPlayer.hand.cards shouldBe List(island)
      updatedState.discardPile shouldBe List(ranger)
      updatedState.turnPhase shouldBe MustDiscard
    }

    "not draw from discard pile for invalid index" in {
      val gameState =
        emptyStateWithDeck(Deck(Nil)).copy(discardPile = List(ranger))

      val updatedState = gameState.drawFromDiscardPileForCurrentPlayer(10)

      updatedState shouldBe gameState
    }

    "discard a card from current player and switch player" in {
      val gameState =
        GameState(
          players = List(
            Player(PlayerOneName, Hand(List(ranger, island))),
            Player(PlayerTwoName, Hand(Nil))
          ),
          currentPlayerIndex = 0,
          deck = Deck(Nil),
          turnPhase = MustDiscard
        )

      val updatedState = gameState.discardCardFromCurrentPlayer(0)

      updatedState.players(0).hand.cards shouldBe List(island)
      updatedState.discardPile shouldBe List(ranger)
      updatedState.currentPlayerIndex shouldBe 1
      updatedState.turnPhase shouldBe MustDraw
    }

    "not discard a card for invalid index" in {
      val gameState =
        GameState(
          players = List(
            Player(PlayerOneName, Hand(List(ranger))),
            Player(PlayerTwoName, Hand(Nil))
          ),
          currentPlayerIndex = 0,
          deck = Deck(Nil),
          turnPhase = MustDiscard
        )

      val updatedState = gameState.discardCardFromCurrentPlayer(5)

      updatedState shouldBe gameState
    }

    "finish the game when discard pile reaches the maximum size" in {
      val nineDiscardedCards = List.fill(9)(ranger)

      val gameState =
        GameState(
          players = List(
            Player(PlayerOneName, Hand(List(island))),
            Player(PlayerTwoName, Hand(Nil))
          ),
          currentPlayerIndex = 0,
          deck = Deck(Nil),
          discardPile = nineDiscardedCards,
          turnPhase = MustDiscard
        )

      val updatedState = gameState.discardCardFromCurrentPlayer(0)

      updatedState.running shouldBe false
    }

    "switch to the next player" in {
      val gameState = emptyStateWithDeck(Deck(List(ranger)))

      val updatedState = gameState.switchPlayer

      updatedState.currentPlayerIndex shouldBe 1
      updatedState.currentPlayer.name shouldBe PlayerTwoName
    }

    "switch back to the first player after the second player" in {
      val gameState =
        emptyStateWithDeck(Deck(List(ranger))).copy(currentPlayerIndex = 1)

      val updatedState = gameState.switchPlayer

      updatedState.currentPlayerIndex shouldBe 0
      updatedState.currentPlayer.name shouldBe PlayerOneName
    }

    "stop the game" in {
      val gameState = emptyStateWithDeck(Deck(List(ranger)))

      val stoppedState = gameState.stop

      stoppedState.running shouldBe false
    }

    "be running by default" in {
      val gameState = emptyStateWithDeck(Deck(List(ranger)))

      gameState.running shouldBe true
    }
  }