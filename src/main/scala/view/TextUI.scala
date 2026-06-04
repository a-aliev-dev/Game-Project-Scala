package view

import controller.GameController
import scala.util.Try

class TextUI(controller: GameController):

  def start(): Unit =
    var running = true

    while running do
      println()
      println(s"=== ${controller.currentPlayer.name} ist am Zug ===")
      println("1. Karte ziehen")
      println("2. Hand anzeigen")
      println("3. Punkte anzeigen")
      println("4. Anzahl Karten im Deck anzeigen")
      println("5. Zug beenden")
      println("6. Undo")
      println("7. Redo")
      println("0. Beenden")
      print("Auswahl: ")

      val input = scala.io.StdIn.readLine()
      val choice = Try(input.trim.toInt).toOption

      choice match
        case Some(1) =>
          controller.drawCard() match
            case Some(card) =>
              println(s"Gezogene Karte: ${card.name} (${card.cardType}, ${card.basePoints})")
            case None =>
              println("Das Deck ist leer.")

        case Some(2) =>
          if controller.currentPlayerHand.cards.isEmpty then
            println("Keine Karten auf der Hand.")
          else
            controller.currentPlayerHand.cards.zipWithIndex.foreach { case (card, index) =>
              println(s"${index + 1}. ${card.name} (${card.cardType}, ${card.basePoints})")
            }

        case Some(3) =>
          println(s"Gesamtpunkte: ${controller.currentPlayerPoints}")

        case Some(4) =>
          println(s"Karten im Deck: ${controller.deckSize}")

        case Some(5) =>
          controller.switchPlayer()
          println(s"Zug beendet. Jetzt ist ${controller.currentPlayer.name} dran.")

        case Some(6) =>
          controller.undo()
          println("Undo ausgefuehrt.")

        case Some(7) =>
          controller.redo()
          println("Redo ausgefuehrt.")

        case Some(0) =>
          controller.stopGame()
          println("Spiel beendet.")
          running = false

        case _ =>
          println("Ungueltige Eingabe. Bitte waehle 1, 2, 3, 4, 5, 6, 7 oder 0.")