package di

import com.google.inject.{AbstractModule, Provides, Singleton}
import controller.{ControllerInterface, GameController, UndoManager, UndoManagerInterface}
import model.score.{FantasyRealmsScoreStrategy, ScoreStrategy}

class GameModule(playerName1: String, playerName2: String) extends AbstractModule:

  override def configure(): Unit =
    bind(classOf[ScoreStrategy]).toInstance(FantasyRealmsScoreStrategy)
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])

  @Provides
  @Singleton
  def provideController(
      scoreStrategy: ScoreStrategy,
      undoManager: UndoManagerInterface
  ): ControllerInterface =
    GameController(playerName1, playerName2, scoreStrategy, undoManager)
