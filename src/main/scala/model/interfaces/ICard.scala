package model.interfaces

import model.CardType

trait ICard:
  def name: String
  def cardType: CardType
  def basePoints: Int
  def bonus: String
  def penalty: String
