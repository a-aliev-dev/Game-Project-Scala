import controller.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DrawCardCommandSpec extends AnyWordSpec with Matchers:

  "A DrawCardCommand" should {

    "draw a card when executed" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      val cardsBefore = controller.currentPlayerHand.cards.size

      command.doStep()

      controller.currentPlayerHand.cards.size shouldBe cardsBefore + 1
    }

    "restore the previous state when undone" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      val stateBefore = controller.getGameState

      command.doStep()
      command.undoStep()

      controller.getGameState shouldBe stateBefore
    }

    "redo the draw action" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      command.doStep()
      val sizeAfterDo = controller.currentPlayerHand.cards.size

      command.undoStep()
      command.redoStep()

      controller.currentPlayerHand.cards.size shouldBe sizeAfterDo
    }

    "not fail when undo is called before execution" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      noException should be thrownBy command.undoStep()
    }
  }