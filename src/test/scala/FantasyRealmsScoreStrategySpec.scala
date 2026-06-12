import model.*
import model.score.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class FantasyRealmsScoreStrategySpec extends AnyWordSpec with Matchers:

  "FantasyRealmsScoreStrategy" should {

    "calculate base points for cards without effects" in {
      val hand = Hand(
        List(
          Card("Rangers", CardType.Army, 5, "Clears the word Army from all penalties"),
          Card("Island", CardType.Flood, 14, "Clears penalty on one Flood or Flame")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) should be >= 19
    }

    "apply King bonus for armies" in {
      val hand = Hand(
        List(
          Card("King", CardType.Leader, 8, "+5 for each Army; +20 for each Army if with Queen"),
          Card("Rangers", CardType.Army, 5, "Clears the word Army from all penalties")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 18
    }

    "apply King and Queen stronger army bonus" in {
      val hand = Hand(
        List(
          Card("King", CardType.Leader, 8, "+5 for each Army; +20 for each Army if with Queen"),
          Card("Queen", CardType.Leader, 6, "+5 for each Army; +20 for each Army if with King"),
          Card("Rangers", CardType.Army, 5, "Clears the word Army from all penalties")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 59
    }

    "blank Knights without a Leader" in {
      val hand = Hand(
        List(
          Card("Knights", CardType.Army, 20, "", "Blanked unless with any Leader")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 0
    }

    "not blank Knights with a Leader" in {
      val hand = Hand(
        List(
          Card("Knights", CardType.Army, 20, "", "Blanked unless with any Leader"),
          Card("King", CardType.Leader, 8, "+5 for each Army; +20 for each Army if with Queen")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 33
    }

    "apply Dragon penalty without a Wizard" in {
      val hand = Hand(
        List(
          Card("Dragon", CardType.Beast, 30, "", "-40 unless with any Wizard")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe -10
    }

    "clear Dragon penalty with a Wizard" in {
      val hand = Hand(
        List(
          Card("Dragon", CardType.Beast, 30, "", "-40 unless with any Wizard"),
          Card("Beastmaster", CardType.Wizard, 9, "+9 for each Beast")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 48
    }

    "apply Rainstorm bonus for Floods" in {
      val hand = Hand(
        List(
          Card("Rainstorm", CardType.Weather, 8, "+10 for each Flood", "Blanks all Flames except Lightning"),
          Card("Island", CardType.Flood, 14, "Clears penalty on one Flood or Flame")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 32
    }

    "blank Flame with Rainstorm except Lightning" in {
      val hand = Hand(
        List(
          Card("Rainstorm", CardType.Weather, 8, "+10 for each Flood", "Blanks all Flames except Lightning"),
          Card("Forge", CardType.Flame, 9, "+9 for each Weapon and Artifact"),
          Card("Lightning", CardType.Flame, 11, "+30 with Rainstorm")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 49
    }

    "apply Magic Wand bonus with Wizard" in {
      val hand = Hand(
        List(
          Card("Magic Wand", CardType.Weapon, 1, "+25 with any Wizard"),
          Card("Beastmaster", CardType.Wizard, 9, "+9 for each Beast")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 35
    }

    "apply Candle combo" in {
      val hand = Hand(
        List(
          Card("Candle", CardType.Flame, 2, "+100 with Book of Changes, Bell Tower, and any Wizard"),
          Card("Book of Changes", CardType.Artifact, 3, "May change the suit of one other card"),
          Card("Bell Tower", CardType.Land, 8, "+15 with any Wizard"),
          Card("Beastmaster", CardType.Wizard, 9, "+9 for each Beast")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) should be >= 137
    }

    "apply Gem of Order run bonus" in {
      val hand = Hand(
        List(
          Card("Magic Wand", CardType.Weapon, 1),
          Card("Princess", CardType.Leader, 2),
          Card("Book of Changes", CardType.Artifact, 3),
          Card("Warlord", CardType.Leader, 4),
          Card("Gem of Order", CardType.Artifact, 5)
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) should be >= 75
    }

    "apply Protection Rune to clear penalties" in {
      val hand = Hand(
        List(
          Card("Protection Rune", CardType.Artifact, 1, "Clears penalties on all cards"),
          Card("Dragon", CardType.Beast, 30, "", "-40 unless with any Wizard")
        )
      )

      FantasyRealmsScoreStrategy.calculate(hand) shouldBe 31
    }
  }