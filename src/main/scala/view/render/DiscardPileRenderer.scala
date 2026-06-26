package view.render

import model.interfaces.ICard

class DiscardPileRenderer(cards: List[ICard]) extends Renderer:

  override protected def title: String =
    "Ablagestapel"

  override protected def content: String =
    if cards.isEmpty then
      "Der Ablagestapel ist leer."
    else
      cards.zipWithIndex
        .map { case (card, index) =>
          s"${index + 1}. ${card.name} (${card.cardType}, ${card.basePoints})"
        }
        .mkString("\n")