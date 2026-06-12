import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class HandSpec extends AnyWordSpec with Matchers:

  private val ranger = Card("Ranger", CardType.Army, 5)
  private val island = Card("Island", CardType.Flood, 14)

  "A Hand" should {

    "return card names" in {
      val hand = Hand(List(ranger, island))

      hand.names shouldBe List("Ranger", "Island")
    }

    "calculate total base points" in {
      val hand = Hand(List(ranger, island))

      hand.totalBasePoints shouldBe 19
    }

    "filter cards by type" in {
      val hand = Hand(List(ranger, island))

      hand.cardsOfType(CardType.Army) shouldBe List(ranger)
    }

    "add a card" in {
      val hand = Hand(List(ranger))

      hand.addCard(island).cards shouldBe List(ranger, island)
    }

    "remove a card by index" in {
      val hand = Hand(List(ranger, island))

      val (removedCard, updatedHand) = hand.removeCardAt(0)

      removedCard shouldBe Some(ranger)
      updatedHand.cards shouldBe List(island)
    }

    "not remove a card for an invalid index" in {
      val hand = Hand(List(ranger))

      val (removedCard, updatedHand) = hand.removeCardAt(5)

      removedCard shouldBe None
      updatedHand shouldBe hand
    }
  }