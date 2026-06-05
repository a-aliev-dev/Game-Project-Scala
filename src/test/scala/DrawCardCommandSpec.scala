import controller.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DrawCardCommandSpec extends AnyWordSpec with Matchers:

  "A DrawCardCommand" should {

    "draw a card when executed" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      controller.currentPlayerHand.cards.size shouldBe 0

      command.doStep()

      controller.currentPlayerHand.cards.size shouldBe 1
    }

    "store and restore the previous state when undone" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      val stateBefore = controller.getGameState

      command.doStep()
      command.undoStep()

      controller.getGameState shouldBe stateBefore
    }

    "redo the draw action after undo" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      command.doStep()
      val handAfterDraw = controller.currentPlayerHand

      command.undoStep()
      controller.currentPlayerHand.cards shouldBe Nil

      command.redoStep()
      controller.currentPlayerHand shouldBe handAfterDraw
    }

    "not fail when undo is called before doStep" in {
      val controller = GameController("A", "B")
      val command = new DrawCardCommand(controller)

      noException should be thrownBy command.undoStep()
    }
  }