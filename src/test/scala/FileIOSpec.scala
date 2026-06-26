import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import fileio.json.FileIOJson
import fileio.xml.FileIOXml
import model.*
import model.state.*

import java.nio.file.{Files, Paths}

class FileIOSpec extends AnyWordSpec with Matchers:

  private def testGameState: GameState =
    val card1 = Card("Test Knight", CardType.Army, 20, "Bonus 1", "Penalty 1")
    val card2 = Card("Test Dragon", CardType.Beast, 30, "Bonus 2", "Penalty 2")
    val card3 = Card("Test Wizard", CardType.Wizard, 10, "", "")

    GameState(
      players = List(
        Player("Ali", Hand(List(card1))),
        Player("Player 2", Hand(List(card2)))
      ),
      currentPlayerIndex = 1,
      deck = Deck(List(card3)),
      discardPile = List(card1, card2),
      status = Running,
      turnPhase = MustDiscard
    )

  "FileIOJson" should {

    "save and load a GameState correctly" in {
      val fileIO = new FileIOJson()
      val state = testGameState

      fileIO.save(state)
      val loadedState = fileIO.load

      loadedState shouldBe state

      Files.deleteIfExists(Paths.get("gamestate.json"))
    }
  }

  "FileIOXml" should {

    "save and load a GameState correctly" in {
      val fileIO = new FileIOXml()
      val state = testGameState

      fileIO.save(state)
      val loadedState = fileIO.load

      loadedState shouldBe state

      Files.deleteIfExists(Paths.get("gamestate.xml"))
    }
  }