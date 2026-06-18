package view

import controller.GameController
import model.state.*
import scala.util.Try
import util.Observer
import view.render.*

class TextUI(controller: GameController) extends Observer:

  override def update(): Unit =
    println("Spielzustand wurde aktualisiert.")

  def start(): Unit =
    var running = true
    var resultPrinted = false

    while running && controller.isRunning do
      println()
      println(s"=== ${controller.currentPlayer.name} ist am Zug ===")
      println(s"Phase: ${phaseText}")
      println("1. Karte vom Deck ziehen")
      println("2. Karte vom Ablagestapel nehmen")
      println("3. Karte aus Hand abwerfen")
      println("4. Hand anzeigen")
      println("5. Ablagestapel anzeigen")
      println("6. Punkte aktueller Spieler anzeigen")
      println("7. Punkte aller Spieler anzeigen")
      println("8. Anzahl Karten im Deck anzeigen")
      println("9. Undo")
      println("10. Redo")
      println("0. Beenden")
      print("Auswahl: ")

      val input = scala.io.StdIn.readLine()
      val choice = Try(input.trim.toInt).toOption

      choice match
        case Some(1) =>
          controller.drawCard() match
            case Some(card) =>
              println(s"Gezogene Karte: ${formatCard(card)}")
            case None =>
              println("Du kannst gerade keine Karte vom Deck ziehen.")

        case Some(2) =>
          showDiscardPile()
          if controller.discardPile.nonEmpty then
            print("Welche Karte moechtest du nehmen? Nummer: ")
            val index = readIndex()
            controller.drawFromDiscardPile(index) match
              case Some(card) =>
                println(s"Karte aufgenommen: ${formatCard(card)}")
              case None =>
                println("Diese Karte kann nicht genommen werden.")

        case Some(3) =>
          showHand()
          if controller.currentPlayerHand.cards.nonEmpty then
            print("Welche Karte moechtest du abwerfen? Nummer: ")
            val index = readIndex()
            controller.discardCardFromCurrentPlayer(index) match
              case Some(card) =>
                println(s"Abgeworfene Karte: ${formatCard(card)}")
                if controller.isRunning then
                  println(s"Jetzt ist ${controller.currentPlayer.name} dran.")
                else
                  printGameResult()
                  resultPrinted = true
                  running = false
              case None =>
                println("Du kannst gerade keine Karte abwerfen.")

        case Some(4) =>
          showHand()

        case Some(5) =>
          showDiscardPile()

        case Some(6) =>
          println(s"Gesamtpunkte: ${controller.currentPlayerPoints}")

        case Some(7) =>
          showAllPoints()

        case Some(8) =>
          println(s"Karten im Deck: ${controller.deckSize}")

        case Some(9) =>
          controller.undo()
          println("Undo ausgefuehrt.")

        case Some(10) =>
          controller.redo()
          println("Redo ausgefuehrt.")

        case Some(0) =>
          controller.stopGame()
          println("Spiel beendet.")
          printGameResult()
          resultPrinted = true
          running = false

        case _ =>
          println("Ungueltige Eingabe. Bitte waehle eine Zahl aus dem Menue.")

    if !controller.isRunning && !resultPrinted then
      printGameResult()

  private def phaseText: String =
    controller.currentPhase match
      case MustDraw =>
        "Karte ziehen"
      case MustDiscard =>
        "Karte abwerfen"

  private def readIndex(): Int =
    val input = scala.io.StdIn.readLine()
    Try(input.trim.toInt).toOption.getOrElse(0) - 1

  private def showHand(): Unit =
    println(HandRenderer(controller.currentPlayer.name, controller.currentPlayerHand.cards).render())

  private def showDiscardPile(): Unit =
    println(DiscardPileRenderer(controller.discardPile).render())

  private def showAllPoints(): Unit =
    controller.allPlayerPoints.foreach { case (name, points) =>
      println(s"$name: $points Punkte")
    }

  private def printGameResult(): Unit =
    println()
    println("=== Endstand ===")
    showAllPoints()
    controller.winnerName.foreach(winner => println(s"Gewinner: $winner"))

  private def formatCard(card: model.Card): String =
    s"${card.name} (${card.cardType}, ${card.basePoints})"