package view

import com.google.inject.Inject
import controller.ControllerInterface
import model.*
import model.interfaces.ICard
import model.state.*
import scala.swing.*
import scala.swing.event.*
import util.Observer
import java.awt.{Color, Dimension, Font}
import javax.swing.ScrollPaneConstants

class GameGUI @Inject() (controller: ControllerInterface) extends MainFrame with Observer:

  title = "Fantasy Realms GUI"
  preferredSize = new Dimension(1250, 850)

  // ===== Header =====

  private val titleLabel = new Label("Fantasy Realms")
  titleLabel.peer.setFont(new Font("Arial", Font.BOLD, 26))
  titleLabel.horizontalAlignment = Alignment.Center

  private val currentPlayerLabel = new Label("")
  currentPlayerLabel.peer.setFont(new Font("Arial", Font.BOLD, 17))
  currentPlayerLabel.horizontalAlignment = Alignment.Center

  private val phaseLabel = new Label("")
  phaseLabel.peer.setFont(new Font("Arial", Font.PLAIN, 17))
  phaseLabel.horizontalAlignment = Alignment.Center

  private val deckLabel = new Label("")
  deckLabel.peer.setFont(new Font("Arial", Font.PLAIN, 17))
  deckLabel.horizontalAlignment = Alignment.Center

  private val statusLabel = new Label("")
  statusLabel.peer.setFont(new Font("Arial", Font.PLAIN, 17))
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

  private val drawDeckButton = new Button("Karte vom Deck ziehen")
  private val undoButton = new Button("Undo")
  private val redoButton = new Button("Redo")
  private val refreshButton = new Button("Aktualisieren")
  private val stopButton = new Button("Spiel beenden")

  // ===== Layout =====

  contents = new BorderPanel:
    layout(new BoxPanel(Orientation.Vertical):
      border = Swing.EmptyBorder(20, 25, 20, 25)

      contents += titleLabel
      contents += Swing.VStrut(8)

      contents += new FlowPanel:
        contents += currentPlayerLabel
        contents += Swing.HStrut(25)
        contents += phaseLabel
        contents += Swing.HStrut(25)
        contents += deckLabel
        contents += Swing.HStrut(25)
        contents += statusLabel

      contents += Swing.VStrut(15)

      contents += sectionLabel("Spielerhand:")
      contents += new ScrollPane(handCardsPanel):
        preferredSize = new Dimension(1250, 300)

      contents += Swing.VStrut(18)

      contents += sectionLabel("Ablagestapel:")
      contents += new ScrollPane(discardCardsPanel):
        preferredSize = new Dimension(1250, 300)

      contents += Swing.VStrut(18)

      contents += sectionLabel("Punkte:")
      contents += new ScrollPane(pointsArea):
        preferredSize = new Dimension(1000, 60)
    ) = BorderPanel.Position.Center

    layout(new GridPanel(3, 1):
      border = Swing.EmptyBorder(10, 10, 10, 10)

      contents += new FlowPanel:
        contents += drawDeckButton
        contents += undoButton
        contents += redoButton
        contents += refreshButton

      contents += new FlowPanel:
        contents += new Label("Tipp: Klicke auf eine Karte im Ablagestapel, um sie zu nehmen.")

      contents += new FlowPanel:
        contents += new Label("Tipp: Klicke auf eine Handkarte, um sie abzuwerfen.")
        contents += Swing.HStrut(25)
        contents += stopButton
    ) = BorderPanel.Position.South

  // ===== Events =====

  listenTo(
    drawDeckButton,
    undoButton,
    redoButton,
    refreshButton,
    stopButton
  )

  reactions += {
    case ButtonClicked(`drawDeckButton`) =>
      controller.drawCard()
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
        handCardsPanel.contents += createHandCardPanel(card, index)
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
        discardCardsPanel.contents += createDiscardCardPanel(card, index)
      }

    discardCardsPanel.revalidate()
    discardCardsPanel.repaint()

  // ===== Card Rendering =====

  private def createHandCardPanel(card: ICard, index: Int): BoxPanel =
    createCardPanel(
      card = card,
      number = index + 1,
      clickHint = "Klicken zum Abwerfen",
      onClick = () =>
        if controller.currentPhase == MustDiscard then
          controller.discardCardFromCurrentPlayer(index)
        else
          showMessage("Du musst zuerst eine Karte ziehen, bevor du eine Handkarte abwerfen kannst.")
        refresh()
    )

  private def createDiscardCardPanel(card: ICard, index: Int): BoxPanel =
    createCardPanel(
      card = card,
      number = index + 1,
      clickHint = "Klicken zum Aufnehmen",
      onClick = () =>
        if controller.currentPhase == MustDraw then
          controller.drawFromDiscardPile(index)
        else
          showMessage("Du hast in diesem Zug bereits eine Karte gezogen. Jetzt musst du eine Karte abwerfen.")
        refresh()
    )

  private def createCardPanel(card: ICard, number: Int, clickHint: String, onClick: () => Unit): BoxPanel =
    val cardColor = colorForCardType(card.cardType)

    new BoxPanel(Orientation.Vertical):
      preferredSize = new Dimension(190, 265)
      minimumSize = new Dimension(190, 265)
      maximumSize = new Dimension(190, 265)
      background = cardColor
      opaque = true
      peer.setToolTipText(clickHint)
      peer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR))
      listenTo(mouse.clicks)
      reactions += {
        case _: MouseClicked =>
          onClick()
      }

      border = Swing.CompoundBorder(
        Swing.LineBorder(Color.DARK_GRAY, 2),
        Swing.EmptyBorder(10, 10, 10, 10)
      )

      contents += cardHeader(s"Karte $number")
      contents += cardHint(clickHint)
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

  private def cardHint(text: String): Label =
    val label = new Label(text)
    label.peer.setFont(new Font("Arial", Font.PLAIN, 11))
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

  private def descriptionText(card: ICard): String =
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
        
      case CardType.Wild =>
        new Color(255, 245, 200)
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

  private def showMessage(message: String): Unit =
    Dialog.showMessage(
      parent = this,
      message = message,
      title = "Hinweis",
      messageType = Dialog.Message.Info
    )