import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameStateSpec extends AnyWordSpec with Matchers:

  private val PlayerOneName = "Spieler 1"
  private val PlayerTwoName = "Spieler 2"

  val ranger = Card("Ranger", CardType.Army, 5)
  val island = Card("Island", CardType.Flood, 14)
  val fireElemental = Card("Fire Elemental", CardType.Flame, 4)

  "A GameState" should {

    "draw one card from the deck and add it to the current player's hand" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(List(ranger, island))

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        deck = deck
      )

      val updatedState = gameState.drawCardForCurrentPlayer

      updatedState.players(0).hand.cards.shouldBe(List(ranger))
      updatedState.players(1).hand.cards.shouldBe(Nil)
      updatedState.deck.cards.shouldBe(List(island))
    }

    "not change when drawing from an empty deck" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(Nil)

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        deck = deck
      )

      val updatedState = gameState.drawCardForCurrentPlayer

      updatedState.shouldBe(gameState)
    }

    "draw a card for a specific player" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(List(ranger, island))

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        deck = deck
      )

      val updatedState = gameState.drawCardForPlayer(1)

      updatedState.players(0).hand.cards.shouldBe(Nil)
      updatedState.players(1).hand.cards.shouldBe(List(ranger))
      updatedState.deck.cards.shouldBe(List(island))
    }

    "deal starting cards to both players" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(List(ranger, island, fireElemental))

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        deck = deck
      )

      val updatedState = gameState.dealStartingCards(1)

      updatedState.players(0).hand.cards.shouldBe(List(ranger))
      updatedState.players(1).hand.cards.shouldBe(List(island))
      updatedState.deck.cards.shouldBe(List(fireElemental))
    }

    "switch to the next player" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(List(ranger))

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        deck = deck
      )

      val updatedState = gameState.switchPlayer

      updatedState.currentPlayerIndex.shouldBe(1)
      updatedState.currentPlayer.name.shouldBe(PlayerTwoName)
    }

    "switch back to the first player after the second player" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(List(ranger))

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 1,
        deck = deck
      )

      val updatedState = gameState.switchPlayer

      updatedState.currentPlayerIndex.shouldBe(0)
      updatedState.currentPlayer.name.shouldBe(PlayerOneName)
    }

    "stop the game" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(List(ranger))

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        deck = deck
      )

      val stoppedState = gameState.stop

      stoppedState.running.shouldBe(false)
    }

    "be running by default" in {
      val player1 = Player(PlayerOneName, Hand(Nil))
      val player2 = Player(PlayerTwoName, Hand(Nil))
      val deck = Deck(List(ranger))

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        deck = deck
      )

      gameState.running.shouldBe(true)
    }
  }