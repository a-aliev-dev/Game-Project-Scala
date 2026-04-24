case class Card(
  name: String,
  cardType: CardType,
  basePoints: Int,
  bonus: String = "",
  penalty: String = ""
)