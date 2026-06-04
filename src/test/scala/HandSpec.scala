import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class HandSpec extends AnyWordSpec with Matchers:

  val ranger = Card("Ranger", CardType.Army, 5)
  val island = Card("Island", CardType.Flood, 14)
  val fireElemental = Card("Fire Elemental", CardType.Flame, 4)

  "A Hand" should {

    "return the names of all cards" in {
      val hand = Hand(List(ranger, island, fireElemental))
      hand.names.shouldBe(List("Ranger", "Island", "Fire Elemental"))
    }

    "calculate the total base points correctly" in {
      val hand = Hand(List(ranger, island, fireElemental))
      hand.totalBasePoints.shouldBe(23)
    }

    "return only cards of the requested type" in {
      val hand = Hand(List(ranger, island, fireElemental))
      hand.cardsOfType(CardType.Flame).shouldBe(List(fireElemental))
    }

    "return an empty list when no card of that type exists" in {
      val hand = Hand(List(ranger, island))
      hand.cardsOfType(CardType.Wizard).shouldBe(Nil)
    }
  }

  "An empty Hand" should {

    "return no names" in {
      val hand = Hand(Nil)
      hand.names.shouldBe(Nil)
    }

    "have total base points of 0" in {
      val hand = Hand(Nil)
      hand.totalBasePoints.shouldBe(0)
    }

    "return no cards for any requested type" in {
      val hand = Hand(Nil)
      hand.cardsOfType(CardType.Flame).shouldBe(Nil)
    }
  }
