package di

import com.google.inject.{AbstractModule, Provides, Singleton}
import controller.ControllerInterface
import controller.UndoManager
import controller.UndoManagerInterface
import controller.impl.GameController
import fileio.FileIOInterface
import fileio.json.FileIOJson
import fileio.xml.FileIOXml
import model.score.FantasyRealmsScoreStrategy
import model.score.ScoreStrategy

class GameModule(
    playerName1: String,
    playerName2: String,
    useJsonFileIO: Boolean = true
) extends AbstractModule:

  override def configure(): Unit =
    bind(classOf[ScoreStrategy]).toInstance(FantasyRealmsScoreStrategy)
    bind(classOf[UndoManagerInterface]).to(classOf[UndoManager])

    if useJsonFileIO then
      bind(classOf[FileIOInterface]).to(classOf[FileIOJson])
    else
      bind(classOf[FileIOInterface]).to(classOf[FileIOXml])

  @Provides
  @Singleton
  def provideController(
      scoreStrategy: ScoreStrategy,
      undoManager: UndoManagerInterface,
      fileIO: FileIOInterface
  ): ControllerInterface =
    GameController(playerName1, playerName2, scoreStrategy, undoManager, fileIO)