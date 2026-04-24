// $COVERAGE-OFF$
@main def runGame(): Unit =
  val hand = List(
    Card("Ranger", CardType.Army, 5),
    Card("Island", CardType.Flood, 14),
    Card("Fire Elemental", CardType.Flame, 4)
  )

  val field = PlayingField("Spieler 1", hand)
  println(field.render)
// $COVERAGE-ON$