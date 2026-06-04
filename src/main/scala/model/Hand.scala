package model
case class Hand(cards: List[Card]):
  def names: List[String] =
    cards.map(_.name)

  def totalBasePoints: Int =
    cards.map(_.basePoints).sum

  def cardsOfType(t: CardType): List[Card] =
    cards.filter(_.cardType == t)