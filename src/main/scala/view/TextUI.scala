package view

import controller.GameController

class TextUI(controller: GameController):

  def start(): Unit =
    var running = true

    while running do
      println()
      println("=== Fantastische Reiche ===")
      println("1. Karte ziehen")
      println("2. Hand anzeigen")
      println("3. Punkte anzeigen")
      println("4. Beenden")
      print("Auswahl: ")

      val input = scala.io.StdIn.readLine()

      input match
        case "1" =>
          controller.drawCard() match
            case Some(card) =>
              println(s"Gezogene Karte: ${card.name} (${card.cardType}, ${card.basePoints})")
            case None =>
              println("Das Deck ist leer.")

        case "2" =>
          controller.currentPlayerHand.cards.zipWithIndex.foreach { case (card, index) =>
            println(s"${index + 1}. ${card.name} (${card.cardType}, ${card.basePoints})")
          }

        case "3" =>
          println(s"Gesamtpunkte: ${controller.currentPlayerPoints}")

        case "4" =>
          println("Spiel beendet.")
          running = false

        case _ =>
          println("Ungueltige Eingabe.")