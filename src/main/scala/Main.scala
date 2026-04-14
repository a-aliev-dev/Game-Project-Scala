enum CardType:
  case Army, Flood, Flame, Wizard, Land, Weather, Leader, Beast, Weapon, Artifact, Wild

case class Card(name: String, cardType: CardType, basePoints: Int)

case class PlayingField(playerName: String, hand: List[Card]):
  def render: String =
    val handOutput =
      if hand.isEmpty then
        "No cards in hand"
      else
        hand.zipWithIndex
          .map((card, index) => s"${index + 1}. ${card.name} (${card.cardType}, ${card.basePoints})")
          .mkString("\n")

    s"""=== Fantastische Reiche ===
       |Player: $playerName
       |
       |Hand:
       |$handOutput
       |""".stripMargin

@main def runGame(): Unit =
  val hand = List(
    Card("Ranger", CardType.Army, 5),
    Card("Island", CardType.Flood, 14),
    Card("Fire Elemental", CardType.Flame, 4)
  )

  val field = PlayingField("Spieler 1", hand)
  println(field.render)