enum CardType:
  case Army, Wizard, Leader, Beast, Weapon, Artifact, Land, Weather, Flood, Flame, Wild

case class Card(
  name: String,
  cardType: CardType,
  basePoints: Int
)

val ranger = Card("Ranger", CardType.Army, 5)
val island = Card("Island", CardType.Flood, 14)
val fireElemental = Card("Fire Elemental", CardType.Flame, 4)

val hand = List(ranger, island, fireElemental)

hand
hand.map(_.name)
hand.map(_.basePoints)
hand.map(_.basePoints).sum
hand.filter(_.cardType == CardType.Flame)