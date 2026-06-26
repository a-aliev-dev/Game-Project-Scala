package model.interfaces

import model.CardType

trait IHand:
  def cards: List[ICard]
  def names: List[String]
  def totalBasePoints: Int
  def cardsOfType(t: CardType): List[ICard]
  def addCard(card: ICard): IHand
  def removeCardAt(index: Int): (Option[ICard], IHand)
