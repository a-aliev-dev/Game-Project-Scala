import model.*
import model.score.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ScoreStrategySpec extends AnyWordSpec with Matchers:

  "BaseScoreStrategy" should {

    "calculate the sum of base points in a hand" in {
      val cards = List(
        Card("Ranger", CardType.Army, 5),
        Card("Island", CardType.Flood, 14)
      )

      val hand = Hand(cards)

      BaseScoreStrategy.calculate(hand) shouldBe 19
    }

    "return zero for an empty hand" in {
      BaseScoreStrategy.calculate(Hand(Nil)) shouldBe 0
    }
  }