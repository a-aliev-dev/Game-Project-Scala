enum CardType:
  case Army, Wizard, Leader, Beast, Weapon, Artifact, Land, Weather, Flood, Flame, Wild

case class Card(
  name: String,
  cardType: CardType,
  basePoints: Int
)

case class Hand(cards: List[Card]):
  def names: List[String] =
    cards.map(_.name)

  def totalBasePoints: Int =
    cards.map(_.basePoints).sum

  def cardsOfType(t: CardType): List[Card] =
    cards.filter(_.cardType == t)

val ranger = Card("Ranger", CardType.Army, 5)
val island = Card("Island", CardType.Flood, 14)
val fireElemental = Card("Fire Elemental", CardType.Flame, 4)

val hand = Hand(List(ranger, island, fireElemental))

hand
hand.names
hand.totalBasePoints
hand.cardsOfType(CardType.Flame)