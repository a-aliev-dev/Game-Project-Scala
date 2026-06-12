package model

case class Hand(cards: List[Card]):

  def names: List[String] =
    cards.map(_.name)

  def totalBasePoints: Int =
    cards.map(_.basePoints).sum

  def cardsOfType(t: CardType): List[Card] =
    cards.filter(_.cardType == t)

  def addCard(card: Card): Hand =
    copy(cards = cards :+ card)

  def removeCardAt(index: Int): (Option[Card], Hand) =
    if index < 0 || index >= cards.size then
      (None, this)
    else
      val removedCard = cards(index)
      val updatedCards = cards.patch(index, Nil, 1)
      (Some(removedCard), copy(cards = updatedCards))