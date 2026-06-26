package fileio

import model.interfaces.IGameState

trait FileIOInterface:
  def load: IGameState
  def save(gameState: IGameState): Unit