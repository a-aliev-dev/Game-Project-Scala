import controller.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class UndoManagerSpec extends AnyWordSpec with Matchers:

  private class TestCommand extends Command:
    var doCount = 0
    var undoCount = 0
    var redoCount = 0

    override def doStep(): Unit =
      doCount += 1

    override def undoStep(): Unit =
      undoCount += 1

    override def redoStep(): Unit =
      redoCount += 1

  "An UndoManager" should {

    "execute a command" in {
      val manager = new UndoManager()
      val command = new TestCommand()

      manager.doStep(command)

      command.doCount shouldBe 1
    }

    "undo an executed command" in {
      val manager = new UndoManager()
      val command = new TestCommand()

      manager.doStep(command)
      manager.undoStep()

      command.undoCount shouldBe 1
    }

    "redo an undone command" in {
      val manager = new UndoManager()
      val command = new TestCommand()

      manager.doStep(command)
      manager.undoStep()
      manager.redoStep()

      command.redoCount shouldBe 1
    }

    "do nothing when undo stack is empty" in {
      val manager = new UndoManager()

      noException should be thrownBy manager.undoStep()
    }

    "do nothing when redo stack is empty" in {
      val manager = new UndoManager()

      noException should be thrownBy manager.redoStep()
    }
  }