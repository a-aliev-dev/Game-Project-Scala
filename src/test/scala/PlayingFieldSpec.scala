import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayingFieldSpec extends AnyWordSpec with Matchers:

  "A PlayingField" should {

    "render a player with cards in hand" in {
      val hand = List(
        Card("Ranger", CardType.Army, 5),
        Card("Island", CardType.Flood, 14)
      )

      val field = PlayingField("Spieler 1", hand)
      val output = field.render

      output.contains("=== Fantastische Reiche ===").shouldBe(true)
      output.contains("Player: Spieler 1").shouldBe(true)
      output.contains("1. Ranger (Army, 5)").shouldBe(true)
      output.contains("2. Island (Flood, 14)").shouldBe(true)
    }

    "render a message when the hand is empty" in {
      val field = PlayingField("Spieler 1", Nil)
      val output = field.render

      output.contains("No cards in hand").shouldBe(true)
    }
  }