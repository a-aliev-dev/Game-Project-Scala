import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers:

  val ranger = Card("Ranger", CardType.Army, 5)
  val island = Card("Island", CardType.Flood, 14)

  "A Player" should {

    "add a card to the hand" in {
      val player = Player("Spieler 1", Hand(List(ranger)))

      val updatedPlayer = player.addCard(island)

      updatedPlayer.hand.cards.shouldBe(List(ranger, island))
    }

    "not change the original player when adding a card" in {
      val player = Player("Spieler 1", Hand(List(ranger)))

      val updatedPlayer = player.addCard(island)

      player.hand.cards.shouldBe(List(ranger))
      updatedPlayer.hand.cards.shouldBe(List(ranger, island))
    }

    "calculate base points from the hand" in {
      val player = Player("Spieler 1", Hand(List(ranger, island)))

      player.basePoints.shouldBe(19)
    }

    "return 0 base points when the hand is empty" in {
      val player = Player("Spieler 1", Hand(Nil))

      player.basePoints.shouldBe(0)
    }
  }
  