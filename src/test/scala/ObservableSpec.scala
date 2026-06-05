import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import util.{Observable, Observer}

class ObservableSpec extends AnyWordSpec with Matchers:

  "An Observable" should {
    "notify registered observers" in {
      val observable = new Observable {}

      var wasUpdated = false

      val observer = new Observer:
        override def update(): Unit =
          wasUpdated = true

      observable.addObserver(observer)
      observable.notifyObservers()

      wasUpdated shouldBe true
    }

    "not notify removed observers" in {
      val observable = new Observable {}

      var updateCount = 0

      val observer = new Observer:
        override def update(): Unit =
          updateCount += 1

      observable.addObserver(observer)
      observable.removeObserver(observer)
      observable.notifyObservers()

      updateCount shouldBe 0
    }
  }