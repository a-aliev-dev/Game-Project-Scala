import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers:

  private val PlayerOneName = "Spieler 1"
  private val ranger = Card("Ranger", CardType.Army, 5)
  private val island = Card("Island", CardType.Flood, 14)

  "A Player" should {

    "add a card to the hand" in {
      val player = Player(PlayerOneName, Hand(List(ranger)))

      val updatedPlayer = player.addCard(island)

      updatedPlayer.hand.cards shouldBe List(ranger, island)
    }

    "not change the original player when adding a card" in {
      val player = Player(PlayerOneName, Hand(List(ranger)))

      val updatedPlayer = player.addCard(island)

      player.hand.cards shouldBe List(ranger)
      updatedPlayer.hand.cards shouldBe List(ranger, island)
    }

    "remove a card from the hand" in {
      val player = Player(PlayerOneName, Hand(List(ranger, island)))

      val (removedCard, updatedPlayer) = player.removeCardAt(1)

      removedCard shouldBe Some(island)
      updatedPlayer.hand.cards shouldBe List(ranger)
    }

    "not remove a card for an invalid index" in {
      val player = Player(PlayerOneName, Hand(List(ranger)))

      val (removedCard, updatedPlayer) = player.removeCardAt(10)

      removedCard shouldBe None
      updatedPlayer shouldBe player
    }

    "calculate base points from the hand" in {
      val player = Player(PlayerOneName, Hand(List(ranger, island)))

      player.basePoints shouldBe 19
    }

    "return 0 base points when the hand is empty" in {
      val player = Player(PlayerOneName, Hand(Nil))

      player.basePoints shouldBe 0
    }
  }