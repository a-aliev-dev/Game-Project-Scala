import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DeckSpec extends AnyWordSpec with Matchers:

  val ranger = Card("Ranger", CardType.Army, 5)
  val island = Card("Island", CardType.Flood, 14)

  "A Deck" should {

    "not be empty when it contains cards" in {
      val deck = Deck(List(ranger, island))

      deck.isEmpty.shouldBe(false)
    }

    "be empty when it contains no cards" in {
      val deck = Deck(Nil)

      deck.isEmpty.shouldBe(true)
    }

    "return its size" in {
      val deck = Deck(List(ranger, island))

      deck.size.shouldBe(2)
    }

    "draw the first card and return the remaining deck" in {
      val deck = Deck(List(ranger, island))

      val (drawnCard, newDeck) = deck.draw

      drawnCard.shouldBe(Some(ranger))
      newDeck.cards.shouldBe(List(island))
    }

    "return None when drawing from an empty deck" in {
      val deck = Deck(Nil)

      val (drawnCard, newDeck) = deck.draw

      drawnCard.shouldBe(None)
      newDeck.shouldBe(deck)
    }

    "create a shuffled deck with the same cards" in {
      val cards = List(ranger, island)

      val deck = Deck.shuffled(cards)

      deck.cards.size.shouldBe(cards.size)
      deck.cards.toSet.shouldBe(cards.toSet)
    }
  }