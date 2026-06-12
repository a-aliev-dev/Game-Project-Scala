import model.*
import model.score.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ScoreStrategySpec extends AnyWordSpec with Matchers:

  "BaseScoreStrategy" should {

    "calculate the sum of base points in a hand" in {
      val hand = Hand(
        List(
          Card("Ranger", CardType.Army, 5),
          Card("Island", CardType.Flood, 14)
        )
      )

      BaseScoreStrategy.calculate(hand) shouldBe 19
    }

    "return zero for an empty hand" in {
      BaseScoreStrategy.calculate(Hand(Nil)) shouldBe 0
    }
  }

  "DoubleScoreStrategy" should {

    "calculate double base points" in {
      val hand = Hand(
        List(
          Card("Ranger", CardType.Army, 5),
          Card("Island", CardType.Flood, 14)
        )
      )

      DoubleScoreStrategy.calculate(hand) shouldBe 38
    }

    "return zero for an empty hand" in {
      DoubleScoreStrategy.calculate(Hand(Nil)) shouldBe 0
    }
  }

  "EmptyHandBonusStrategy" should {

    "return a bonus for an empty hand" in {
      EmptyHandBonusStrategy.calculate(Hand(Nil)) shouldBe 10
    }

    "return normal base points for a non-empty hand" in {
      val hand = Hand(
        List(
          Card("Ranger", CardType.Army, 5),
          Card("Island", CardType.Flood, 14)
        )
      )

      EmptyHandBonusStrategy.calculate(hand) shouldBe 19
    }
  }