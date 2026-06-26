import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import util.Observer
import model.*
import model.score.*
import model.state.*

class GameControllerSpec extends AnyWordSpec with Matchers:

  "A GameController" should {

    "deal starting cards" in {
      val controller = GameController("A", "B")

      controller.dealStartingCards(2)

      controller.getGameState.players(0).hand.cards.size shouldBe 2
      controller.getGameState.players(1).hand.cards.size shouldBe 2
      controller.currentPhase shouldBe MustDraw
    }

    "notify observers when a card is drawn" in {
      val controller = GameController("A", "B")

      var updateCount = 0

      val observer = new Observer:
        override def update(): Unit =
          updateCount += 1

      controller.addObserver(observer)
      controller.drawCard()

      updateCount should be > 0
    }

    "notify observers when switching player" in {
      val controller = GameController("A", "B")

      var wasUpdated = false

      val observer = new Observer:
        override def update(): Unit =
          wasUpdated = true

      controller.addObserver(observer)
      controller.switchPlayer()

      wasUpdated shouldBe true
      controller.currentPlayer.name shouldBe "B"
    }

    "notify observers when setting a new game state" in {
      val controller = GameController("A", "B")

      var wasUpdated = false

      val observer = new Observer:
        override def update(): Unit =
          wasUpdated = true

      controller.addObserver(observer)

      val stoppedState = controller.getGameState.stop
      controller.setGameState(stoppedState)

      wasUpdated shouldBe true
      controller.isRunning shouldBe false
    }

    "stop the game" in {
      val controller = GameController("A", "B")

      controller.isRunning shouldBe true
      controller.stopGame()
      controller.isRunning shouldBe false
    }

    "return the current player" in {
      val controller = GameController("A", "B")

      controller.currentPlayer.name shouldBe "A"
    }

    "return the current player's hand" in {
      val controller = GameController("A", "B")

      controller.currentPlayerHand.cards shouldBe Nil
    }

    "return the current player's points using the default score strategy" in {
      val controller = GameController("A", "B")

      controller.currentPlayerPoints shouldBe 0
    }

    "use an injected score strategy" in {
      val controller = GameController("A", "B", EmptyHandBonusStrategy)

      controller.currentPlayerPoints shouldBe 10
    }

    "return the deck size" in {
      val controller = GameController("A", "B")

      controller.deckSize should be > 0
    }

    "draw a card and return it" in {
      val controller = GameController("A", "B")

      val drawnCard = controller.drawCard()

      drawnCard should not be empty
      controller.currentPlayerHand.cards.size shouldBe 1
      controller.currentPhase shouldBe MustDiscard
    }

    "not draw twice in the same turn" in {
      val controller = GameController("A", "B")

      controller.drawCard()

      controller.drawCard() shouldBe None
    }

    "draw a card directly and return it" in {
      val controller = GameController("A", "B")

      val drawnCard = controller.drawCardDirectly()

      drawnCard should not be empty
      controller.currentPlayerHand.cards.size shouldBe 1
      controller.currentPhase shouldBe MustDiscard
    }

    "return None when drawing directly from an empty deck" in {
      val controller = GameController("A", "B")

      val emptyState = GameState(
        players = List(
          Player("A", Hand(Nil)),
          Player("B", Hand(Nil))
        ),
        currentPlayerIndex = 0,
        deck = Deck(Nil)
      )

      controller.setGameState(emptyState)

      controller.drawCardDirectly() shouldBe None
    }

    "return None when drawing through command from an empty deck" in {
      val controller = GameController("A", "B")

      val emptyState = GameState(
        players = List(
          Player("A", Hand(Nil)),
          Player("B", Hand(Nil))
        ),
        currentPlayerIndex = 0,
        deck = Deck(Nil)
      )

      controller.setGameState(emptyState)

      controller.drawCard() shouldBe None
    }

    "draw from discard pile" in {
      val controller = GameController("A", "B")
      val card = Card("Ranger", CardType.Army, 5)

      val stateWithDiscard =
        controller.getGameState.asInstanceOf[model.GameState].copy(discardPile = List(card), turnPhase = MustDraw)

      controller.setGameState(stateWithDiscard)

      controller.drawFromDiscardPile(0) shouldBe Some(card)
      controller.currentPlayerHand.cards shouldBe List(card)
      controller.discardPile shouldBe Nil
      controller.currentPhase shouldBe MustDiscard
    }

    "not draw from discard pile with invalid index" in {
      val controller = GameController("A", "B")

      controller.drawFromDiscardPile(99) shouldBe None
    }

    "discard a card and switch player" in {
      val controller = GameController("A", "B")

      controller.drawCard()
      val cardToDiscard = controller.currentPlayerHand.cards.head

      controller.discardCardFromCurrentPlayer(0) shouldBe Some(cardToDiscard)
      controller.discardPile shouldBe List(cardToDiscard)
      controller.currentPlayer.name shouldBe "B"
      controller.currentPhase shouldBe MustDraw
    }

    "not discard before drawing" in {
      val controller = GameController("A", "B")

      controller.discardCardFromCurrentPlayer(0) shouldBe None
    }

    "not discard invalid index" in {
      val controller = GameController("A", "B")

      controller.drawCard()

      controller.discardCardFromCurrentPlayer(99) shouldBe None
    }

    "return all player points" in {
      val controller = GameController("A", "B")

      controller.allPlayerPoints.map(_._1) shouldBe List("A", "B")
    }

    "return no winner while the game is running" in {
      val controller = GameController("A", "B")

      controller.winnerName shouldBe None
    }

    "return a winner when the game is stopped" in {
      val controller = GameController("A", "B")

      controller.stopGame()

      controller.winnerName should not be empty
    }

    "switch to the next player" in {
      val controller = GameController("A", "B")

      controller.currentPlayer.name shouldBe "A"

      controller.switchPlayer()

      controller.currentPlayer.name shouldBe "B"
    }

    "undo a drawn card" in {
      val controller = GameController("A", "B")

      controller.drawCard()
      controller.currentPlayerHand.cards.size shouldBe 1

      controller.undo()

      controller.currentPlayerHand.cards.size shouldBe 0
      controller.currentPhase shouldBe MustDraw
    }

    "redo a drawn card after undo" in {
      val controller = GameController("A", "B")

      controller.drawCard()
      val handSizeAfterDraw = controller.currentPlayerHand.cards.size

      controller.undo()
      controller.currentPlayerHand.cards.size shouldBe 0

      controller.redo()
      controller.currentPlayerHand.cards.size shouldBe handSizeAfterDraw
    }

    "not fail when undo is called without previous commands" in {
      val controller = GameController("A", "B")

      noException should be thrownBy controller.undo()
    }

    "not fail when redo is called without previous undo" in {
      val controller = GameController("A", "B")

      noException should be thrownBy controller.redo()
    }
  }