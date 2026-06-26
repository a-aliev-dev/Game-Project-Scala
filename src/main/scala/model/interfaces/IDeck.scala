package model.interfaces

trait IDeck:
  def cards: List[ICard]
  def isEmpty: Boolean
  def size: Int
  def draw: (Option[ICard], IDeck)
