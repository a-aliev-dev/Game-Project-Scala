import com.google.inject.Inject
import controller.ControllerInterface
import view.{GameGUI, TextUI}

class GameApplication @Inject() (
    controller: ControllerInterface,
    tui: TextUI,
    gui: GameGUI
):

  def start(): Unit =
    controller.addObserver(tui)
    controller.addObserver(gui)
    controller.dealStartingCards(7)

    gui.showGUI()
    tui.start()
