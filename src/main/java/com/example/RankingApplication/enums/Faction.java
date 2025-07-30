package com.example.RankingApplication.enums;

import lombok.Getter;

@Getter
public enum Faction {

    CHAOS("Chaos", 1),
    SPACE_WOLVES("Space Wolves", 1),
    ORKS("Orks", 1),
    DEATHWATCH("Deathwatch", 1),
    THOUSAND_SONS("Thousand Sons", 1),
    DEATH_GUARD("Death Guard", 1),
    NECRONS("Necrons", 1),
    BLACK_TEMPLARS("Black Templars", 1),
    TAU_EMPIRE("T'au Empire", 1),
    CHAOS_KNIGHTS("Chaos Knights", 1),
    BLOOD_ANGELS("Blood Angels", 1),
    IMPERIUM("Imperium", 1),
    WORLD_EATERS("World Eaters", 1),
    ASTRA_MILITARUM("Astra Militarum", 1),
    CHAOS_DAEMONS("Chaos Daemons", 1),
    DRUKHARI("Drukhari", 1),
    GREY_KNIGHTS("Grey Knights", 1),
    ADEPTA_SORORITAS("Adepta Sororitas", 1),
    ADEPTUS_MECHANICUS("Adeptus Mechanicus", 1),
    FORCES_OF_THE_HIVE_MIND("Forces of the Hive Mind", 1),
    GENESTEALER_CULT("Genestealer Cult", 1),
    EMPERORS_CHILDREN("Emperor's Children", 1),
    IMPERIAL_KNIGHTS("Imperial Knights", 1),
    DARK_ANGELS("Dark Angels", 1),
    SPACE_MARINES("Space Marines (Astartes)", 1),
    ADEPTUS_CUSTODES("Adeptus Custodes", 1),
    TYRANIDS("Tyranids", 1),
    CHAOS_SPACE_MARINES("Chaos Space Marines", 1),
    AELDARI("Aeldari", 1),
    LEAGUES_OF_VOTANN("Leagues of Votann", 1),
    XENOS("Xenos", 1),
    IMPERIAL_AGENTS("Imperial Agents", 1),


    BONESPLITTERZ("Bonesplitterz", 4),
    DISCIPLES_OF_TZEENTCH("Disciples of Tzeentch", 4),
    FLESH_EATER_COURTS("Flesh-eater Courts", 4),
    STORMCAST_ETERNALS("Stormcast Eternals", 4),
    BLADES_OF_KHORNE("Blades of Khorne", 4),
    KRULEBOYZ("Kruleboyz", 4),
    FYRESLAYERS("Fyreslayers", 4),
    IRONJAWZ("Ironjawz", 4),
    SERAPHON("Seraphon", 4),
    SYLVANETH("Sylvaneth", 4),
    MAGGOTKIN_OF_NURGLE("Maggotkin of Nurgle", 4),
    NIGHTHAUNT("Nighthaunt", 4),
    SLAVES_TO_DARKNESS("Slaves to Darkness", 4),
    HEDONITES_OF_SLAANESH("Hedonites of Slaanesh", 4),
    KHARADRON_OVERLORDS("Kharadron Overlords", 4),
    DAUGHTERS_OF_KHAINE("Daughters of Khaine", 4),
    BEASTS_OF_CHAOS("Beasts of Chaos", 4),
    GLOOMSPITE_GITZ("Gloomspite Gitz", 4),
    IDONETH_DEEPKIN("Idoneth Deepkin", 4),
    SKAVEN_AOS("Skaven", 4),
    CITIES_OF_SIGMAR("Cities of Sigmar", 4),
    OGOR_MAWTRIBES("Ogor Mawtribes", 4),
    OSSIARCH_BONEREAPERS("Ossiarch Bonereapers", 4),
    LUMINETH_REALM_LORDS("Lumineth Realm-Lords", 4),
    SONS_OF_BEHEMAT("Sons of Behemat", 4),
    SOULBLIGHT_GRAVELORDS("Soulblight Gravelords", 4),

    EMPIRE_OF_MAN("Empire of Man", 89),
    DWARFEN_MOUNTAIN_HOLDS("Dwarfen Mountain Holds", 89),
    KINGDOM_OF_BRETONNIA("Kingdom of Bretonnia", 89),
    WOOD_ELF_REALMS("Wood Elf Realms", 89),
    HIGH_ELF_REALMS("High Elf Realms", 89),
    ORC_AND_GOBLIN_TRIBES("Orc & Goblin Tribes", 89),
    WARRIORS_OF_CHAOS("Warriors of Chaos", 89),
    BEASTMEN_BRAYHERDS("Beastmen Brayherds", 89),
    TOMB_KINGS_OF_KHEMRI("Tomb Kings of Khemri", 89),
    DARK_ELVES("Dark Elves", 89),
    SKAVEN("Skaven", 89),
    VAMPIRE_COUNTS("Vampire Counts", 89),
    DAEMONS_OF_CHAOS("Daemons of Chaos", 89),
    OGRE_KINGDOMS("Ogre Kingdoms", 89),
    LIZARDMEN("Lizardmen", 89),
    CHAOS_DWARFS("Chaos Dwarfs", 89),
    EXPEDITIONARY_FORCE("Expeditionary Force", 89),
    ROYAL_CLAN("Royal Clan", 89),
    BRETONNIAN_EXILES("Bretonnian Exiles", 89),
    ERRANTRY_CRUSADE("Errantry Crusade", 89),
    NOMADIC_WAAAGH("Nomadic Waaagh!", 89),
    TROLL_HORDE("Troll Horde", 89),
    MORTUARY_CULT("Mortuary Cult", 89),
    ROYAL_HOST("Royal Host", 89),
    WOLVES_OF_THE_SEA("Wolves of the Sea", 89),
    CATHAY("Cathay", 89),

    DEFAULT_TOW("Sin ejército", 89),
    DEFAULT_40k("Sin ejército", 1),
    DEFAULT_AOS("Sin ejército", 4);
    // Método para obtener el valor
    private final String displayName;
    private final int systemCode;

    // Constructor para asignar el valor al enum
    Faction(String displayName, int systemCode) {
        this.displayName = displayName;
        this.systemCode = systemCode;
    }

    public boolean isDefault() {
        return this == DEFAULT_TOW || this == DEFAULT_40k || this == DEFAULT_AOS;
    }

    public static Faction getDefaultForSystemCode(int systemCode) {
        return switch (systemCode) {
            case 1 -> Faction.DEFAULT_40k;
            case 4 -> Faction.DEFAULT_AOS;
            case 89 -> Faction.DEFAULT_TOW;
            default -> throw new IllegalArgumentException("Código de sistema no reconocido: " + systemCode);
        };
    }


}

