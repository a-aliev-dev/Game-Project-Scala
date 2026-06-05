import controller.GameController
import view.TextUI
import scala.io.StdIn.readLine

@main def runGame(): Unit =
  println("=== Fantasy Realms ===")
  println("Willkommen im Spiel!")
  println()

  val enteredName1 = readLine("Name von Spieler 1: ")
  val enteredName2 = readLine("Name von Spieler 2: ")

  val playerName1 =
    if enteredName1.trim.isEmpty then "Spieler 1" else enteredName1.trim

  val playerName2 =
    if enteredName2.trim.isEmpty then "Spieler 2" else enteredName2.trim

  
  val controller = GameController(playerName1, playerName2)
  val ui = TextUI(controller)

  controller.addObserver(ui)

  ui.start()