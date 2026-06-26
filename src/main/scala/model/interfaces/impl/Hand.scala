package model

import model.interfaces.{ICard, IHand}

case class Hand(cards: List[Card]) extends IHand:

  def names: List[String] =
    cards.map(_.name)

  def totalBasePoints: Int =
    cards.map(_.basePoints).sum

  def cardsOfType(t: CardType): List[ICard] =
    cards.filter(_.cardType == t)

  def addCard(card: ICard): IHand =
    card match
      case c: Card => copy(cards = cards :+ c)
      case _       => this

  def removeCardAt(index: Int): (Option[ICard], IHand) =
    if index < 0 || index >= cards.size then
      (None, this)
    else
      val removedCard = cards(index)
      val updatedCards = cards.patch(index, Nil, 1)
      (Some(removedCard), copy(cards = updatedCards))
