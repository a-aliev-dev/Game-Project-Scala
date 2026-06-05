import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import util.Observer
import model.*

class GameControllerSpec extends AnyWordSpec with Matchers:

  "A GameController" should {

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

    "return the current player's points" in {
      val controller = GameController("A", "B")

      controller.currentPlayerPoints shouldBe 0
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
    }

    "draw a card directly and return it" in {
      val controller = GameController("A", "B")

      val drawnCard = controller.drawCardDirectly()

      drawnCard should not be empty
      controller.currentPlayerHand.cards.size shouldBe 1
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