package view.render

import model.interfaces.ICard

class HandRenderer(playerName: String, cards: List[ICard]) extends Renderer:

  override protected def title: String =
    s"Hand von $playerName"

  override protected def content: String =
    if cards.isEmpty then
      "Keine Karten auf der Hand."
    else
      cards.zipWithIndex
        .map { case (card, index) =>
          s"${index + 1}. ${card.name} (${card.cardType}, ${card.basePoints})"
        }
        .mkString("\n")