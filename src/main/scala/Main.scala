import com.google.inject.Guice
import di.GameModule
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

  val injector = Guice.createInjector(new GameModule(playerName1, playerName2, useJsonFileIO = true))
  val application = injector.getInstance(classOf[GameApplication])

  application.start()
