package model.score

import model.interfaces.IHand

trait ScoreStrategy:
  def calculate(hand: IHand): Int

object BaseScoreStrategy extends ScoreStrategy:
  override def calculate(hand: IHand): Int =
    hand.totalBasePoints

object DoubleScoreStrategy extends ScoreStrategy:
  override def calculate(hand: IHand): Int =
    hand.totalBasePoints * 2

object EmptyHandBonusStrategy extends ScoreStrategy:
  override def calculate(hand: IHand): Int =
    if hand.cards.isEmpty then 10
    else hand.totalBasePoints