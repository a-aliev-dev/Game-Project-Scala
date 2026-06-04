package model.score

import model.Hand

trait ScoreStrategy:
  def calculate(hand: Hand): Int

object BaseScoreStrategy extends ScoreStrategy:
  override def calculate(hand: Hand): Int =
    hand.totalBasePoints