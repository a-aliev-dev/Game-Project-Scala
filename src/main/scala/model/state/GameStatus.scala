package model.state

sealed trait GameStatus
case object Running extends GameStatus
case object Finished extends GameStatus

sealed trait TurnPhase
case object MustDraw extends TurnPhase
case object MustDiscard extends TurnPhase