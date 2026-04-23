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