import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import util.Observer

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

    "stop the game" in {
      val controller = GameController("A", "B")

      controller.isRunning shouldBe true
      controller.stopGame()
      controller.isRunning shouldBe false
    }
  }