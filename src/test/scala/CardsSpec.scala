import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class CardsSpec extends AnyWordSpec with Matchers:

  "Cards" should {

    "contain 50 cards" in {
      Cards.allCards.size.shouldBe(50)
    }

    "not contain duplicate card names" in {
      val names = Cards.allCards.map(_.name)
      names.distinct.size.shouldBe(names.size)
    }

    "only contain cards with non-empty names" in {
      Cards.allCards.forall(_.name.nonEmpty).shouldBe(true)
    }

    "only contain cards with valid base points" in {
      Cards.allCards.forall(_.basePoints >= 0).shouldBe(true)
    }
  }