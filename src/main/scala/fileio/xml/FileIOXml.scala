package fileio.xml

import fileio.FileIOInterface
import model.Card
import model.CardType
import model.Deck
import model.GameState
import model.Hand
import model.Player
import model.interfaces.*
import model.state.*

import java.io.PrintWriter
import scala.xml.*

class FileIOXml extends FileIOInterface:

  private val fileName = "gamestate.xml"

  override def save(gameState: IGameState): Unit =
    val xml = gameStateToXml(gameState)
    val writer = new PrintWriter(fileName)

    try
      writer.write(xml.toString())
    finally
      writer.close()

  override def load: IGameState =
    val xml = XML.loadFile(fileName)
    xmlToGameState(xml)

  private def gameStateToXml(gameState: IGameState): Elem =
    <gameState>
      <players>
        {gameState.players.map(playerToXml)}
      </players>
      <currentPlayerIndex>{gameState.currentPlayerIndex}</currentPlayerIndex>
      {deckToXml(gameState.deck)}
      <discardPile>
        {gameState.discardPile.map(cardToXml)}
      </discardPile>
      <status>{gameStatusToString(gameState.status)}</status>
      <turnPhase>{turnPhaseToString(gameState.turnPhase)}</turnPhase>
    </gameState>

  private def playerToXml(player: IPlayer): Elem =
    <player>
      <name>{player.name}</name>
      {handToXml(player.hand)}
    </player>

  private def handToXml(hand: IHand): Elem =
    <hand>
      <cards>
        {hand.cards.map(cardToXml)}
      </cards>
    </hand>

  private def deckToXml(deck: IDeck): Elem =
    <deck>
      <cards>
        {deck.cards.map(cardToXml)}
      </cards>
    </deck>

  private def cardToXml(card: ICard): Elem =
    <card>
      <name>{card.name}</name>
      <cardType>{card.cardType.toString}</cardType>
      <basePoints>{card.basePoints}</basePoints>
      <bonus>{card.bonus}</bonus>
      <penalty>{card.penalty}</penalty>
    </card>

  private def xmlToGameState(xml: Elem): GameState =
    val players =
      (xml \ "players" \ "player").map(xmlToPlayer).toList

    val currentPlayerIndex =
      (xml \ "currentPlayerIndex").text.toInt

    val deck =
      xmlToDeck((xml \ "deck").head)

    val discardPile =
      (xml \ "discardPile" \ "card").map(xmlToCard).toList

    val status =
      stringToGameStatus((xml \ "status").text)

    val turnPhase =
      stringToTurnPhase((xml \ "turnPhase").text)

    GameState(
      players = players,
      currentPlayerIndex = currentPlayerIndex,
      deck = deck,
      discardPile = discardPile,
      status = status,
      turnPhase = turnPhase
    )

  private def xmlToPlayer(node: Node): Player =
    Player(
      name = (node \ "name").text,
      hand = xmlToHand((node \ "hand").head)
    )

  private def xmlToHand(node: Node): Hand =
    val cards =
      (node \ "cards" \ "card").map(xmlToCard).toList

    Hand(cards)

  private def xmlToDeck(node: Node): Deck =
    val cards =
      (node \ "cards" \ "card").map(xmlToCard).toList

    Deck(cards)

  private def xmlToCard(node: Node): Card =
    Card(
      name = (node \ "name").text,
      cardType = stringToCardType((node \ "cardType").text),
      basePoints = (node \ "basePoints").text.toInt,
      bonus = (node \ "bonus").text,
      penalty = (node \ "penalty").text
    )

  private def stringToCardType(value: String): CardType =
  CardType.valueOf(value)

  private def gameStatusToString(status: GameStatus): String =
    status match
      case Running  => "Running"
      case Finished => "Finished"

  private def stringToGameStatus(value: String): GameStatus =
    value match
      case "Running"  => Running
      case "Finished" => Finished
      case other      => throw new IllegalArgumentException(s"Unknown GameStatus: $other")

  private def turnPhaseToString(turnPhase: TurnPhase): String =
    turnPhase match
      case MustDraw    => "MustDraw"
      case MustDiscard => "MustDiscard"

  private def stringToTurnPhase(value: String): TurnPhase =
    value match
      case "MustDraw"    => MustDraw
      case "MustDiscard" => MustDiscard
      case other         => throw new IllegalArgumentException(s"Unknown TurnPhase: $other")