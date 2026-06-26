package model.interfaces

trait IPlayer:
  def name: String
  def hand: IHand
  def addCard(card: ICard): IPlayer
  def removeCardAt(index: Int): (Option[ICard], IPlayer)
  def basePoints: Int
