package view

import controller.GameController
import model.*
import model.state.*
import scala.swing.*
import scala.swing.event.*
import util.Observer
import java.awt.{Color, Dimension, Font}

class GameGUI(controller: GameController) extends MainFrame with Observer:

  title = "Fantasy Realms GUI"
  preferredSize = new Dimension(1250, 850)

  // ===== Header =====

  private val titleLabel = new Label("Fantasy Realms")
  titleLabel.peer.setFont(new Font("Arial", Font.BOLD, 30))
  titleLabel.horizontalAlignment = Alignment.Center

  private val currentPlayerLabel = new Label("")
  currentPlayerLabel.peer.setFont(new Font("Arial", Font.BOLD, 20))
  currentPlayerLabel.horizontalAlignment = Alignment.Center

  private val phaseLabel = new Label("")
  phaseLabel.peer.setFont(new Font("Arial", Font.PLAIN, 18))
  phaseLabel.horizontalAlignment = Alignment.Center

  private val deckLabel = new Label("")
  deckLabel.peer.setFont(new Font("Arial", Font.PLAIN, 18))
  deckLabel.horizontalAlignment = Alignment.Center

  private val statusLabel = new Label("")
  statusLabel.peer.setFont(new Font("Arial", Font.PLAIN, 18))
  statusLabel.horizontalAlignment = Alignment.Center

  // ===== Card Panels =====

  private val handCardsPanel = new FlowPanel:
    hGap = 15
    vGap = 15

  private val discardCardsPanel = new FlowPanel:
    hGap = 15
    vGap = 15

  // ===== Scores =====

  private val pointsArea = new TextArea:
    editable = false
    lineWrap = true
    wordWrap = true
    rows = 4
    peer.setFont(new Font("Arial", Font.PLAIN, 15))

  // ===== Controls =====

  private val handIndexField = new TextField:
    columns = 5

  private val discardIndexField = new TextField:
    columns = 5

  private val drawDeckButton = new Button("Karte vom Deck ziehen")
  private val takeDiscardButton = new Button("Vom Ablagestapel nehmen")
  private val discardHandButton = new Button("Karte aus Hand abwerfen")
  private val undoButton = new Button("Undo")
  private val redoButton = new Button("Redo")
  private val refreshButton = new Button("Aktualisieren")
  private val stopButton = new Button("Spiel beenden")

  // ===== Layout =====

  contents = new BorderPanel:
    layout(new BoxPanel(Orientation.Vertical):
      border = Swing.EmptyBorder(20, 25, 20, 25)

      contents += titleLabel
      contents += Swing.VStrut(10)
      contents += currentPlayerLabel
      contents += phaseLabel
      contents += deckLabel
      contents += statusLabel
      contents += Swing.VStrut(25)

      contents += sectionLabel("Spielerhand:")
      contents += new ScrollPane(handCardsPanel):
        preferredSize = new Dimension(1150, 290)

      contents += Swing.VStrut(18)

      contents += sectionLabel("Ablagestapel:")
      contents += new ScrollPane(discardCardsPanel):
        preferredSize = new Dimension(1150, 250)

      contents += Swing.VStrut(18)

      contents += sectionLabel("Punkte:")
      contents += new ScrollPane(pointsArea):
        preferredSize = new Dimension(1150, 90)
    ) = BorderPanel.Position.Center

    layout(new GridPanel(4, 1):
      border = Swing.EmptyBorder(10, 10, 10, 10)

      contents += new FlowPanel:
        contents += drawDeckButton
        contents += undoButton
        contents += redoButton
        contents += refreshButton

      contents += new FlowPanel:
        contents += new Label("Ablagestapel Nummer:")
        contents += discardIndexField
        contents += takeDiscardButton

      contents += new FlowPanel:
        contents += new Label("Handkarten Nummer:")
        contents += handIndexField
        contents += discardHandButton

      contents += new FlowPanel:
        contents += stopButton
    ) = BorderPanel.Position.South

  // ===== Events =====

  listenTo(
    drawDeckButton,
    takeDiscardButton,
    discardHandButton,
    undoButton,
    redoButton,
    refreshButton,
    stopButton
  )

  reactions += {
    case ButtonClicked(`drawDeckButton`) =>
      controller.drawCard()
      refresh()

    case ButtonClicked(`takeDiscardButton`) =>
      readIndex(discardIndexField.text) match
        case Some(index) =>
          controller.drawFromDiscardPile(index)
        case None =>
          showMessage("Bitte eine gueltige Nummer fuer den Ablagestapel eingeben.")
      refresh()

    case ButtonClicked(`discardHandButton`) =>
      readIndex(handIndexField.text) match
        case Some(index) =>
          controller.discardCardFromCurrentPlayer(index)
        case None =>
          showMessage("Bitte eine gueltige Nummer fuer die Handkarte eingeben.")
      refresh()

    case ButtonClicked(`undoButton`) =>
      controller.undo()
      refresh()

    case ButtonClicked(`redoButton`) =>
      controller.redo()
      refresh()

    case ButtonClicked(`refreshButton`) =>
      refresh()

    case ButtonClicked(`stopButton`) =>
      controller.stopGame()
      refresh()
  }

  // ===== Observer =====

  override def update(): Unit =
    Swing.onEDT {
      refresh()
    }

  def showGUI(): Unit =
    Swing.onEDT {
      visible = true
      refresh()
    }

  // ===== Refresh =====

  private def refresh(): Unit =
    currentPlayerLabel.text = s"Aktueller Spieler: ${controller.currentPlayer.name}"
    phaseLabel.text = s"Phase: ${phaseText}"
    deckLabel.text = s"Karten im Deck: ${controller.deckSize}"
    statusLabel.text = s"Spielstatus: ${if controller.isRunning then "Laeuft" else "Beendet"}"

    refreshHandCards()
    refreshDiscardCards()
    pointsArea.peer.setText(pointsText)

  private def refreshHandCards(): Unit =
    handCardsPanel.contents.clear()

    val cards = controller.currentPlayerHand.cards

    if cards.isEmpty then
      handCardsPanel.contents += new Label("Keine Karten auf der Hand.")
    else
      cards.zipWithIndex.foreach { case (card, index) =>
        handCardsPanel.contents += createCardPanel(card, index + 1)
      }

    handCardsPanel.revalidate()
    handCardsPanel.repaint()

  private def refreshDiscardCards(): Unit =
    discardCardsPanel.contents.clear()

    val cards = controller.discardPile

    if cards.isEmpty then
      discardCardsPanel.contents += new Label("Der Ablagestapel ist leer.")
    else
      cards.zipWithIndex.foreach { case (card, index) =>
        discardCardsPanel.contents += createCardPanel(card, index + 1)
      }

    discardCardsPanel.revalidate()
    discardCardsPanel.repaint()

  // ===== Card Rendering =====

  private def createCardPanel(card: Card, number: Int): BoxPanel =
    val cardColor = colorForCardType(card.cardType)

    new BoxPanel(Orientation.Vertical):
      preferredSize = new Dimension(190, 245)
      minimumSize = new Dimension(190, 245)
      maximumSize = new Dimension(190, 245)
      background = cardColor
      opaque = true

      border = Swing.CompoundBorder(
        Swing.LineBorder(Color.DARK_GRAY, 2),
        Swing.EmptyBorder(10, 10, 10, 10)
      )

      contents += cardHeader(s"Karte $number")
      contents += Swing.VStrut(5)
      contents += cardTitle(card.name, cardColor)
      contents += Swing.VStrut(8)
      contents += cardText(s"Typ: ${card.cardType}", cardColor, 1)
      contents += cardText(s"Punkte: ${card.basePoints}", cardColor, 1)
      contents += Swing.VStrut(8)
      contents += cardText("Beschreibung:", cardColor, 1)
      contents += cardText(descriptionText(card), cardColor, 6)

  private def cardHeader(text: String): Label =
    val label = new Label(text)
    label.peer.setFont(new Font("Arial", Font.BOLD, 14))
    label.horizontalAlignment = Alignment.Center
    label

  private def cardTitle(text: String, backgroundColor: Color): TextArea =
    val area = new TextArea(text):
      editable = false
      lineWrap = true
      wordWrap = true
      rows = 2
      opaque = true
      background = backgroundColor
      peer.setFont(new Font("Arial", Font.BOLD, 16))
    area

  private def cardText(text: String, backgroundColor: Color, rowCount: Int): TextArea =
    val area = new TextArea(text):
      editable = false
      lineWrap = true
      wordWrap = true
      rows = rowCount
      opaque = true
      background = backgroundColor
      peer.setFont(new Font("Arial", Font.PLAIN, 13))
    area

  private def descriptionText(card: Card): String =
    val bonusText =
      if card.bonus.nonEmpty then s"Bonus: ${card.bonus}"
      else "Bonus: -"

    val penaltyText =
      if card.penalty.nonEmpty then s"Strafe: ${card.penalty}"
      else "Strafe: -"

    s"$bonusText\n$penaltyText"

  private def colorForCardType(cardType: CardType): Color =
    cardType match
      case CardType.Army =>
        new Color(255, 210, 210)

      case CardType.Leader =>
        new Color(255, 235, 170)

      case CardType.Wizard =>
        new Color(225, 210, 255)

      case CardType.Weapon =>
        new Color(220, 220, 220)

      case CardType.Artifact =>
        new Color(230, 220, 200)

      case CardType.Beast =>
        new Color(210, 245, 210)

      case CardType.Land =>
        new Color(220, 235, 190)

      case CardType.Weather =>
        new Color(220, 230, 255)

      case CardType.Flood =>
        new Color(190, 225, 255)

      case CardType.Flame =>
        new Color(255, 220, 185)

  // ===== Helpers =====

  private def sectionLabel(text: String): Label =
    val label = new Label(text)
    label.peer.setFont(new Font("Arial", Font.BOLD, 20))
    label.horizontalAlignment = Alignment.Left
    label

  private def phaseText: String =
    controller.currentPhase match
      case MustDraw =>
        "Karte ziehen"

      case MustDiscard =>
        "Karte abwerfen"

  private def pointsText: String =
    val base =
      controller.allPlayerPoints
        .map { case (name, points) => s"$name: $points Punkte" }
        .mkString("\n")

    if !controller.isRunning then
      controller.winnerName match
        case Some(winner) =>
          s"$base\n\nGewinner: $winner"

        case None =>
          base
    else
      base

  private def readIndex(text: String): Option[Int] =
    text.trim.toIntOption.map(_ - 1)

  private def showMessage(message: String): Unit =
    Dialog.showMessage(
      parent = this,
      message = message,
      title = "Hinweis",
      messageType = Dialog.Message.Info
    )