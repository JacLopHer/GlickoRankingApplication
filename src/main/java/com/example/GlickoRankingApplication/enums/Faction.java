package com.example.GlickoRankingApplication.enums;

public enum Faction {
    CHAOS("Chaos"),
    SPACE_WOLVES("Space Wolves"),
    ORKS("Orks"),
    DEATHWATCH("Deathwatch"),
    THOUSAND_SONS("Thousand Sons"),
    DEATH_GUARD("Death Guard"),
    NECRONS("Necrons"),
    BLACK_TEMPLARS("Black Templars"),
    TAU_EMPIRE("T'au Empire"),
    CHAOS_KNIGHTS("Chaos Knights"),
    BLOOD_ANGELS("Blood Angels"),
    IMPERIUM("Imperium"),
    WORLD_EATERS("World Eaters"),
    ASTRA_MILITARUM("Astra Militarum"),
    CHAOS_DAEMONS("Chaos Daemons"),
    DRUKHARI("Drukhari"),
    GREY_KNIGHTS("Grey Knights"),
    ADEPTA_SORORITAS("Adepta Sororitas"),
    ADEPTUS_MECHANICUS("Adeptus Mechanicus"),
    FORCES_OF_THE_HIVE_MIND("Forces of the Hive Mind"),
    GENESTEALER_CULT("Genestealer Cult"),
    EMPERORS_CHILDREN("Emperor's Children"),
    IMPERIAL_KNIGHTS("Imperial Knights"),
    DARK_ANGELS("Dark Angels"),
    SPACE_MARINES("Space Marines (Astartes)"),
    ADEPTUS_CUSTODES("Adeptus Custodes"),
    TYRANIDS("Tyranids"),
    CHAOS_SPACE_MARINES("Chaos Space Marines"),
    AELDARI("Aeldari"),
    LEAGUES_OF_VOTANN("Leagues of Votann"),
    XENOS("Xenos"),
    IMPERIAL_AGENTS("Imperial Agents");

    private final String displayName;

    // Constructor para asignar el valor al enum
    Faction(String displayName) {
        this.displayName = displayName;
    }

    // Método para obtener el valor
    public String getDisplayName() {
        return displayName;
    }
}

