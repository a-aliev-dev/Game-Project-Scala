package model.score

import model.*

object FantasyRealmsScoreStrategy extends ScoreStrategy:

  private case class EvalCard(index: Int, card: Card, effectiveType: CardType)

  private val allCardTypes: List[CardType] =
    List(
      CardType.Army,
      CardType.Leader,
      CardType.Wizard,
      CardType.Weapon,
      CardType.Artifact,
      CardType.Beast,
      CardType.Land,
      CardType.Weather,
      CardType.Flood,
      CardType.Flame
    )

  override def calculate(hand: Hand): Int =
    val typeOverrideOptions: List[Option[(Int, CardType)]] =
      val baseOption = List(None)

      val bookOptions =
        if hand.cards.exists(_.name == "Book of Changes") then
          for
            index <- hand.cards.indices.toList
            if hand.cards(index).name != "Book of Changes"
            cardType <- allCardTypes
            if cardType != hand.cards(index).cardType
          yield Some(index -> cardType)
        else
          Nil

      baseOption ++ bookOptions

    typeOverrideOptions
      .map(typeOverride => bestScoreWithIslandChoice(hand, typeOverride))
      .maxOption
      .getOrElse(0)

  private def bestScoreWithIslandChoice(
      hand: Hand,
      typeOverride: Option[(Int, CardType)]
  ): Int =
    val cards = buildEvalCards(hand, typeOverride)

    val islandTargets: List[Option[Int]] =
      val hasIsland = cards.exists(_.card.name == "Island")

      if hasIsland then
        None :: cards
          .filter(card => card.effectiveType == CardType.Flood || card.effectiveType == CardType.Flame)
          .map(card => Some(card.index))
      else
        List(None)

    islandTargets
      .map(target => scoreOnce(cards, target))
      .maxOption
      .getOrElse(0)

  private def buildEvalCards(
      hand: Hand,
      typeOverride: Option[(Int, CardType)]
  ): List[EvalCard] =
    hand.cards.zipWithIndex.map { case (card, index) =>
      val effectiveType =
        typeOverride match
          case Some((overrideIndex, newType)) if overrideIndex == index =>
            newType
          case _ =>
            card.cardType

      EvalCard(index, card, effectiveType)
    }

  private def scoreOnce(cards: List[EvalCard], islandPenaltyTarget: Option[Int]): Int =
    val blanked = computeBlankedCards(cards, islandPenaltyTarget)
    val activeCards = cards.filterNot(card => blanked.contains(card.index))

    def hasName(name: String): Boolean =
      activeCards.exists(_.card.name == name)

    def countType(cardType: CardType): Int =
      activeCards.count(_.effectiveType == cardType)

    def countOtherType(card: EvalCard, cardType: CardType): Int =
      activeCards.count(other => other.index != card.index && other.effectiveType == cardType)

    def countOtherName(card: EvalCard, name: String): Int =
      activeCards.count(other => other.index != card.index && other.card.name == name)

    def hasType(cardType: CardType): Boolean =
      activeCards.exists(_.effectiveType == cardType)

    def penaltyApplies(card: EvalCard): Boolean =
      penaltyAppliesFor(cards, card, blanked, islandPenaltyTarget)

    val baseScore =
      activeCards.map(_.card.basePoints).sum

    val effectScore =
      activeCards.map { card =>
        card.card.name match
          case "Knights" =>
            0

          case "Elven Archers" =>
            5 * countType(CardType.Weather)

          case "Light Cavalry" =>
            if penaltyApplies(card) then -2 * countType(CardType.Land)
            else 0

          case "Dwarvish Infantry" =>
            10 * countOtherType(card, CardType.Army)

          case "Rangers" =>
            0

          case "King" =>
            if hasName("Queen") then 20 * countType(CardType.Army)
            else 5 * countType(CardType.Army)

          case "Queen" =>
            if hasName("King") then 20 * countType(CardType.Army)
            else 5 * countType(CardType.Army)

          case "Princess" =>
            8 * (
              countType(CardType.Army) +
                countType(CardType.Wizard) +
                countOtherType(card, CardType.Leader)
            )

          case "Warlord" =>
            4 * countType(CardType.Army)

          case "Empress" =>
            val bonus = 10 * countType(CardType.Army)
            val penalty =
              if penaltyApplies(card) then -5 * countOtherType(card, CardType.Leader)
              else 0

            bonus + penalty

          case "Warlock Lord" =>
            if penaltyApplies(card) then
              -10 * (countType(CardType.Leader) + countOtherType(card, CardType.Wizard))
            else
              0

          case "Enchantress" =>
            5 * (
              countType(CardType.Land) +
                countType(CardType.Weather) +
                countType(CardType.Beast)
            )

          case "Necromancer" =>
            0

          case "Illusionist" =>
            if hasType(CardType.Weather) then 15 else 0

          case "Beastmaster" =>
            9 * countType(CardType.Beast)

          case "Sword of Keth" =>
            if hasName("Shield of Keth") && hasType(CardType.Leader) then 40
            else if hasType(CardType.Leader) then 10
            else 0

          case "Shield of Keth" =>
            if hasName("Sword of Keth") && hasType(CardType.Leader) then 40
            else if hasType(CardType.Leader) then 15
            else 0

          case "Magic Wand" =>
            if hasType(CardType.Wizard) then 25 else 0

          case "Elven Longbow" =>
            if hasName("Elven Archers") || hasName("Warlord") || hasName("Beastmaster") then 30
            else 0

          case "Warship" =>
            0

          case "Book of Changes" =>
            0

          case "Protection Rune" =>
            0

          case "Gem of Order" =>
            gemOfOrderBonus(activeCards)

          case "World Tree" =>
            val allDifferentSuits =
              activeCards.map(_.effectiveType).distinct.size == activeCards.size

            if allDifferentSuits then 50 else 0

          case "War Dirigible" =>
            0

          case "Dragon" =>
            if penaltyApplies(card) && !hasType(CardType.Wizard) then -40
            else 0

          case "Unicorn" =>
            if hasName("Princess") then 30
            else if hasName("Empress") || hasName("Queen") || hasName("Enchantress") then 15
            else 0

          case "Basilisk" =>
            0

          case "Warhorse" =>
            if hasType(CardType.Leader) || hasType(CardType.Wizard) then 14
            else 0

          case "Hydra" =>
            if hasName("Swamp") then 28 else 0

          case "Mountain" =>
            0

          case "Cavern" =>
            if hasName("Dragon") || hasName("Dwarvish Infantry") then 25
            else 0

          case "Bell Tower" =>
            if hasType(CardType.Wizard) then 15 else 0

          case "Forest" =>
            val elvenArchersBonus =
              if hasName("Elven Archers") then 1 else 0

            12 * (countType(CardType.Beast) + elvenArchersBonus)

          case "Earth Elemental" =>
            15 * countType(CardType.Land)

          case "Rainstorm" =>
            10 * countType(CardType.Flood)

          case "Blizzard" =>
            if penaltyApplies(card) then
              val armyPenalty =
                if rangersClearsArmy(cards, blanked) then 0
                else countType(CardType.Army)

              -5 * (
                armyPenalty +
                  countType(CardType.Leader) +
                  countType(CardType.Beast) +
                  countType(CardType.Flame)
              )
            else
              0

          case "Smoke" =>
            0

          case "Whirlwind" =>
            if hasName("Rainstorm") && (hasName("Blizzard") || hasName("Great Flood")) then 40
            else 0

          case "Air Elemental" =>
            15 * countType(CardType.Weather)

          case "Great Flood" =>
            0

          case "Fountain of Life" =>
            activeCards
              .filter(other => other.index != card.index)
              .filter(other =>
                Set(
                  CardType.Weapon,
                  CardType.Flood,
                  CardType.Flame,
                  CardType.Land,
                  CardType.Weather
                ).contains(other.effectiveType)
              )
              .map(_.card.basePoints)
              .maxOption
              .getOrElse(0)

          case "Swamp" =>
            if penaltyApplies(card) then
              val armyPenalty =
                if rangersClearsArmy(cards, blanked) then 0
                else countType(CardType.Army)

              -3 * (armyPenalty + countType(CardType.Flame))
            else
              0

          case "Water Elemental" =>
            15 * countType(CardType.Flood)

          case "Island" =>
            0

          case "Wildfire" =>
            0

          case "Candle" =>
            if hasName("Book of Changes") && hasName("Bell Tower") && hasType(CardType.Wizard) then 100
            else 0

          case "Forge" =>
            9 * (countType(CardType.Weapon) + countType(CardType.Artifact))

          case "Lightning" =>
            if hasName("Rainstorm") then 30 else 0

          case "Fire Elemental" =>
            15 * countType(CardType.Flame)

          case _ =>
            0
      }.sum

    baseScore + effectScore

  private def computeBlankedCards(
      cards: List[EvalCard],
      islandPenaltyTarget: Option[Int]
  ): Set[Int] =
    var blanked: Set[Int] = Set.empty
    var changed = true

    while changed do
      val nextBlanked = calculateBlankedCards(cards, blanked, islandPenaltyTarget)
      changed = nextBlanked != blanked
      blanked = nextBlanked

    blanked

  private def calculateBlankedCards(
      cards: List[EvalCard],
      currentBlanked: Set[Int],
      islandPenaltyTarget: Option[Int]
  ): Set[Int] =
    var blanked: Set[Int] = Set.empty

    def isActive(card: EvalCard): Boolean =
      !currentBlanked.contains(card.index)

    def activeCards: List[EvalCard] =
      cards.filter(isActive)

    def hasName(name: String): Boolean =
      activeCards.exists(_.card.name == name)

    def hasType(cardType: CardType): Boolean =
      activeCards.exists(_.effectiveType == cardType)

    def sourcePenaltyApplies(card: EvalCard): Boolean =
      penaltyAppliesFor(cards, card, currentBlanked, islandPenaltyTarget)

    val armyIsClearedFromPenalties =
      rangersClearsArmy(cards, currentBlanked)

    cards.foreach { card =>
      if sourcePenaltyApplies(card) then
        card.card.name match
          case "Knights" =>
            if !hasType(CardType.Leader) then
              blanked += card.index

          case "Warship" =>
            if !hasType(CardType.Flood) then
              blanked += card.index

          case "War Dirigible" =>
            if !hasType(CardType.Army) then
              blanked += card.index

          case "Smoke" =>
            if !hasType(CardType.Flame) then
              blanked += card.index

          case _ =>
            ()
    }

    cards.foreach { source =>
      if isActive(source) && sourcePenaltyApplies(source) then
        source.card.name match
          case "Basilisk" =>
            cards.foreach { target =>
              val blanksArmy =
                target.effectiveType == CardType.Army && !armyIsClearedFromPenalties

              val blanksLeader =
                target.effectiveType == CardType.Leader

              val blanksOtherBeast =
                target.effectiveType == CardType.Beast && target.index != source.index

              if blanksArmy || blanksLeader || blanksOtherBeast then
                blanked += target.index
            }

          case "Great Flood" =>
            cards.foreach { target =>
              val blanksArmy =
                target.effectiveType == CardType.Army && !armyIsClearedFromPenalties

              val blanksLand =
                target.effectiveType == CardType.Land

              val blanksFlame =
                target.effectiveType == CardType.Flame

              if blanksArmy || blanksLand || blanksFlame then
                blanked += target.index
            }

          case "Rainstorm" =>
            cards.foreach { target =>
              if target.effectiveType == CardType.Flame && target.card.name != "Lightning" then
                blanked += target.index
            }

          case "Blizzard" =>
            cards.foreach { target =>
              if target.effectiveType == CardType.Flood then
                blanked += target.index
            }

          case "Wildfire" =>
            cards.foreach { target =>
              val allowedByType =
                Set(
                  CardType.Flame,
                  CardType.Wizard,
                  CardType.Weather,
                  CardType.Weapon,
                  CardType.Artifact
                ).contains(target.effectiveType)

              val allowedByName =
                Set(
                  "Mountain",
                  "Great Flood",
                  "Island",
                  "Unicorn",
                  "Dragon"
                ).contains(target.card.name)

              if !allowedByType && !allowedByName then
                blanked += target.index
            }

          case _ =>
            ()
    }

    blanked

  private def penaltyAppliesFor(
      cards: List[EvalCard],
      card: EvalCard,
      currentBlanked: Set[Int],
      islandPenaltyTarget: Option[Int]
  ): Boolean =
    def isActive(c: EvalCard): Boolean =
      !currentBlanked.contains(c.index)

    def hasActiveName(name: String): Boolean =
      cards.exists(c => c.card.name == name && isActive(c))

    val protectionRuneClearsAllPenalties =
      hasActiveName("Protection Rune")

    val mountainClearsFloodPenalties =
      card.effectiveType == CardType.Flood && hasActiveName("Mountain")

    val cavernOrIllusionistClearsWeatherPenalties =
      card.effectiveType == CardType.Weather &&
        (hasActiveName("Cavern") || hasActiveName("Illusionist"))

    val islandClearsThisPenalty =
      islandPenaltyTarget.contains(card.index) &&
        (card.effectiveType == CardType.Flood || card.effectiveType == CardType.Flame) &&
        hasActiveName("Island")

    !protectionRuneClearsAllPenalties &&
      !mountainClearsFloodPenalties &&
      !cavernOrIllusionistClearsWeatherPenalties &&
      !islandClearsThisPenalty

  private def rangersClearsArmy(cards: List[EvalCard], currentBlanked: Set[Int]): Boolean =
    cards.exists(card => card.card.name == "Rangers" && !currentBlanked.contains(card.index))

  private def gemOfOrderBonus(activeCards: List[EvalCard]): Int =
    val strengths =
      activeCards.map(_.card.basePoints).distinct.sorted

    val longestRun =
      strengths.foldLeft((0, 0, Option.empty[Int])) {
        case ((best, current, previous), strength) =>
          previous match
            case Some(prev) if strength == prev + 1 =>
              val nextCurrent = current + 1
              (best.max(nextCurrent), nextCurrent, Some(strength))

            case _ =>
              (best.max(1), 1, Some(strength))
      }._1

    longestRun match
      case length if length >= 7 => 150
      case 6                    => 100
      case 5                    => 60
      case 4                    => 30
      case 3                    => 10
      case _                    => 0