package model
case class Player(name: String, hand: Hand):

  def addCard(card: Card): Player =
    copy(hand = Hand(hand.cards :+ card))

  def basePoints: Int =
    hand.totalBasePoints