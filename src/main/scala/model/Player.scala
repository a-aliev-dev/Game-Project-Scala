package model

case class Player(name: String, hand: Hand):

  def addCard(card: Card): Player =
    copy(hand = hand.addCard(card))

  def removeCardAt(index: Int): (Option[Card], Player) =
    val (removedCard, updatedHand) = hand.removeCardAt(index)
    (removedCard, copy(hand = updatedHand))

  def basePoints: Int =
    hand.totalBasePoints