package model.score

import model.Hand

trait ScoreStrategy:
  def calculate(hand: Hand): Int

object BaseScoreStrategy extends ScoreStrategy:
  override def calculate(hand: Hand): Int =
    hand.totalBasePoints

object DoubleScoreStrategy extends ScoreStrategy:
  override def calculate(hand: Hand): Int =
    hand.totalBasePoints * 2

object EmptyHandBonusStrategy extends ScoreStrategy:
  override def calculate(hand: Hand): Int =
    if hand.cards.isEmpty then 10
    else hand.totalBasePoints