case class Deck(cards: List[Card]):

  def isEmpty: Boolean =
    cards.isEmpty

  def size: Int =
    cards.size

  def draw: (Option[Card], Deck) =
    cards match

      case Nil =>
        (None, this)

      case firstCard :: remainingCards =>
        (Some(firstCard), Deck(remainingCards))


object Deck:

  def shuffled(cards: List[Card]): Deck =
    Deck(scala.util.Random.shuffle(cards))