// $COVERAGE-OFF$
import scala.io.StdIn.readLine

@main def runGame(): Unit =
  println("=== Fantasy Realms ===")
  println("Willkommen im Spiel!")
  println()

  val enteredName1 = readLine("Name von Spieler 1: ")
  val enteredName2 = readLine("Name von Spieler 2: ")

  val playerName1 =
    if enteredName1.trim.isEmpty then "Spieler 1"
    else enteredName1.trim

  val playerName2 =
    if enteredName2.trim.isEmpty then "Spieler 2"
    else enteredName2.trim

  val player1 = Player(playerName1, Hand(Nil))
  val player2 = Player(playerName2, Hand(Nil))

  val deck = Deck.shuffled(Cards.allCards)

  var gameState = GameState(
    players = List(player1, player2),
    currentPlayerIndex = 0,
    deck = deck
  )

  while gameState.running do
    val currentPlayer = gameState.currentPlayer

    println()
    println(s"=== ${currentPlayer.name} ist am Zug ===")
    println("1) Hand anzeigen")
    println("2) Karte ziehen")
    println("3) Punkte anzeigen")
    println("4) Anzahl Karten im Deck anzeigen")
    println("5) Zug beenden")
    println("0) Spiel beenden")
    println()

    val input = readLine("Auswahl: ")

    input match
      case "1" =>
        val field = PlayingField(
          currentPlayer.name,
          currentPlayer.hand.cards
        )

        println()
        println(field.render)

      case "2" =>
        if gameState.deck.isEmpty then
          println("Das Deck ist leer. Du kannst keine Karte mehr ziehen.")
        else
          val oldHandSize = gameState.currentPlayer.hand.cards.size

          gameState = gameState.drawCardForCurrentPlayer

          val updatedPlayer = gameState.currentPlayer
          val drawnCard = updatedPlayer.hand.cards.last

          println(s"${updatedPlayer.name} hat eine Karte gezogen: ${drawnCard.name}")
          println(s"Handkarten vorher: $oldHandSize")
          println(s"Handkarten jetzt: ${updatedPlayer.hand.cards.size}")

      case "3" =>
        println(s"Aktuelle Basispunkte von ${currentPlayer.name}: ${currentPlayer.basePoints}")

      case "4" =>
        println(s"Karten im Deck: ${gameState.deck.size}")

      case "5" =>
        gameState = gameState.switchPlayer
        println(s"Zug beendet. Jetzt ist ${gameState.currentPlayer.name} dran.")

      case "0" =>
        gameState = gameState.stop
        println("Spiel beendet.")

      case _ =>
        println("Ungültige Eingabe. Bitte wähle 1, 2, 3, 4, 5 oder 0.")
// $COVERAGE-ON$