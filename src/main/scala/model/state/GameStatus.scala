package model.state

sealed trait GameStatus
case object Running extends GameStatus
case object Finished extends GameStatus