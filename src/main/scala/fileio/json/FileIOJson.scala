package fileio.json

import fileio.FileIOInterface
import model.Card
import model.CardType
import model.Deck
import model.GameState
import model.Hand
import model.Player
import model.interfaces.*
import model.state.*
import play.api.libs.json.*

import java.io.PrintWriter
import scala.io.Source

class FileIOJson extends FileIOInterface:

  private val fileName = "gamestate.json"

  override def save(gameState: IGameState): Unit =
    val json = gameStateToJson(gameState)
    val writer = new PrintWriter(fileName)

    try
      writer.write(Json.prettyPrint(json))
    finally
      writer.close()

  override def load: IGameState =
    val source = Source.fromFile(fileName)

    try
      val json = Json.parse(source.mkString)
      jsonToGameState(json)
    finally
      source.close()

  private def gameStateToJson(gameState: IGameState): JsObject =
    Json.obj(
      "players" -> JsArray(gameState.players.map(playerToJson)),
      "currentPlayerIndex" -> gameState.currentPlayerIndex,
      "deck" -> deckToJson(gameState.deck),
      "discardPile" -> JsArray(gameState.discardPile.map(cardToJson)),
      "status" -> gameStatusToString(gameState.status),
      "turnPhase" -> turnPhaseToString(gameState.turnPhase)
    )

  private def playerToJson(player: IPlayer): JsObject =
    Json.obj(
      "name" -> player.name,
      "hand" -> handToJson(player.hand)
    )

  private def handToJson(hand: IHand): JsObject =
    Json.obj(
      "cards" -> JsArray(hand.cards.map(cardToJson))
    )

  private def deckToJson(deck: IDeck): JsObject =
    Json.obj(
      "cards" -> JsArray(deck.cards.map(cardToJson))
    )

  private def cardToJson(card: ICard): JsObject =
    Json.obj(
      "name" -> card.name,
      "cardType" -> card.cardType.toString,
      "basePoints" -> card.basePoints,
      "bonus" -> card.bonus,
      "penalty" -> card.penalty
    )

  private def jsonToGameState(json: JsValue): GameState =
    val players =
      (json \ "players").as[List[JsValue]].map(jsonToPlayer)

    val currentPlayerIndex =
      (json \ "currentPlayerIndex").as[Int]

    val deck =
      jsonToDeck((json \ "deck").get)

    val discardPile =
      (json \ "discardPile").as[List[JsValue]].map(jsonToCard)

    val status =
      stringToGameStatus((json \ "status").as[String])

    val turnPhase =
      stringToTurnPhase((json \ "turnPhase").as[String])

    GameState(
      players = players,
      currentPlayerIndex = currentPlayerIndex,
      deck = deck,
      discardPile = discardPile,
      status = status,
      turnPhase = turnPhase
    )

  private def jsonToPlayer(json: JsValue): Player =
    Player(
      name = (json \ "name").as[String],
      hand = jsonToHand((json \ "hand").get)
    )

  private def jsonToHand(json: JsValue): Hand =
    val cards =
      (json \ "cards").as[List[JsValue]].map(jsonToCard)

    Hand(cards)

  private def jsonToDeck(json: JsValue): Deck =
    val cards =
      (json \ "cards").as[List[JsValue]].map(jsonToCard)

    Deck(cards)

  private def jsonToCard(json: JsValue): Card =
    Card(
      name = (json \ "name").as[String],
      cardType = stringToCardType((json \ "cardType").as[String]),
      basePoints = (json \ "basePoints").as[Int],
      bonus = (json \ "bonus").as[String],
      penalty = (json \ "penalty").as[String]
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