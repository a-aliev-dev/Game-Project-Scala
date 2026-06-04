package model
object Cards:
  val allCards: List[Card] = List(
    // Army
    Card("Knights", CardType.Army, 20, "", "Blanked unless with any Leader"),
    Card("Elven Archers", CardType.Army, 10, "+5 for each Weather"),
    Card("Light Cavalry", CardType.Army, 17, "", "-2 for each Land"),
    Card("Dwarvish Infantry", CardType.Army, 15, "+10 for each other Army"),
    Card("Rangers", CardType.Army, 5, "Clears the word Army from all penalties"),

    // Leader
    Card("King", CardType.Leader, 8, "+5 for each Army; +20 for each Army if with Queen"),
    Card("Queen", CardType.Leader, 6, "+5 for each Army; +20 for each Army if with King"),
    Card("Princess", CardType.Leader, 2, "+8 for each Army, Wizard, and other Leader"),
    Card("Warlord", CardType.Leader, 4, "+4 for each Army"),
    Card("Empress", CardType.Leader, 15, "+10 for each Army", "-5 for each other Leader"),

    // Wizard
    Card("Warlock Lord", CardType.Wizard, 25, "", "-10 for each Leader and other Wizard"),
    Card("Enchantress", CardType.Wizard, 5, "+5 for each Land, Weather, and Beast"),
    Card("Necromancer", CardType.Wizard, 3, "May take one Army, Leader, Wizard, or Beast from discard at end"),
    Card("Illusionist", CardType.Wizard, 18, "+15 with any Weather; clears penalties on Weather"),
    Card("Beastmaster", CardType.Wizard, 9, "+9 for each Beast"),

    // Weapon
    Card("Sword of Keth", CardType.Weapon, 7, "+10 with any Leader; +40 instead with Shield of Keth and any Leader"),
    Card("Shield of Keth", CardType.Weapon, 4, "+15 with any Leader; +40 instead with Sword of Keth and any Leader"),
    Card("Magic Wand", CardType.Weapon, 1, "+25 with any Wizard"),
    Card("Elven Longbow", CardType.Weapon, 3, "+30 with Elven Archers, Warlord, or Beastmaster"),
    Card("Warship", CardType.Weapon, 23, "", "Blanked unless with any Flood"),

    // Artifact
    Card("Book of Changes", CardType.Artifact, 3, "May change the suit of one other card"),
    Card("Protection Rune", CardType.Artifact, 1, "Clears penalties on all cards"),
    Card("Gem of Order", CardType.Artifact, 5, "+10 for 3-card run, +30 for 4-card run, +60 for 5-card run, +100 for 6-card run, +150 for 7-card run"),
    Card("World Tree", CardType.Artifact, 2, "+50 if every card has a different suit"),
    Card("War Dirigible", CardType.Artifact, 35, "", "Blanked unless with any Army"),

    // Beast
    Card("Dragon", CardType.Beast, 30, "", "-40 unless with any Wizard"),
    Card("Unicorn", CardType.Beast, 9, "+30 with Princess; +15 with Empress, Queen, or Enchantress"),
    Card("Basilisk", CardType.Beast, 35, "", "Blanks all Armies, Leaders, and other Beasts"),
    Card("Warhorse", CardType.Beast, 6, "+14 with any Leader or Wizard"),
    Card("Hydra", CardType.Beast, 12, "+28 with Swamp"),

    // Land
    Card("Mountain", CardType.Land, 9, "Clears penalties on Floods"),
    Card("Cavern", CardType.Land, 6, "+25 with Dragon or Dwarvish Infantry; clears penalties on Weather"),
    Card("Bell Tower", CardType.Land, 8, "+15 with any Wizard"),
    Card("Forest", CardType.Land, 7, "+12 for each Beast and Elven Archers"),
    Card("Earth Elemental", CardType.Land, 4, "+15 for each Land"),

    // Weather
    Card("Rainstorm", CardType.Weather, 8, "+10 for each Flood", "Blanks all Flames except Lightning"),
    Card("Blizzard", CardType.Weather, 30, "", "Blanks all Floods; -5 for each Army, Leader, Beast, and Flame"),
    Card("Smoke", CardType.Weather, 27, "", "Blanked unless with any Flame"),
    Card("Whirlwind", CardType.Weather, 13, "+40 with Rainstorm and either Blizzard or Great Flood"),
    Card("Air Elemental", CardType.Weather, 4, "+15 for each Weather"),

    // Flood
    Card("Great Flood", CardType.Flood, 32, "", "Blanks all Armies, Lands, and Flames"),
    Card("Fountain of Life", CardType.Flood, 1, "Adds base strength of one Weapon, Flood, Flame, Land, or Weather"),
    Card("Swamp", CardType.Flood, 18, "", "-3 for each Army and Flame"),
    Card("Water Elemental", CardType.Flood, 4, "+15 for each Flood"),
    Card("Island", CardType.Flood, 14, "Clears penalty on one Flood or Flame"),

    // Flame
    Card("Wildfire", CardType.Flame, 40, "", "Blanks all cards except Flames, Wizards, Weather, Weapons, Artifacts, Mountain, Great Flood, Island, Unicorn, and Dragon"),
    Card("Candle", CardType.Flame, 2, "+100 with Book of Changes, Bell Tower, and any Wizard"),
    Card("Forge", CardType.Flame, 9, "+9 for each Weapon and Artifact"),
    Card("Lightning", CardType.Flame, 11, "+30 with Rainstorm"),
    Card("Fire Elemental", CardType.Flame, 4, "+15 for each Flame")
  )