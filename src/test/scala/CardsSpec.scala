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

    "contain at least one card of every used card type" in {
      Cards.allCards.exists(_.cardType == CardType.Army).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Leader).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Wizard).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Weapon).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Artifact).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Beast).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Land).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Weather).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Flood).shouldBe(true)
      Cards.allCards.exists(_.cardType == CardType.Flame).shouldBe(true)
    }
  }