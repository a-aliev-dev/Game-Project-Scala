package model

import model.interfaces.{ICard, IHand, IPlayer}

case class Player(name: String, hand: Hand) extends IPlayer:

  def addCard(card: ICard): IPlayer =
    card match
      case c: Card => copy(hand = hand.copy(cards = hand.cards :+ c))
      case _       => this

  def removeCardAt(index: Int): (Option[ICard], IPlayer) =
    val (removedCard, updatedHand) = hand.removeCardAt(index)
    (removedCard, copy(hand = updatedHand.asInstanceOf[Hand]))

  def basePoints: Int =
    hand.totalBasePoints
