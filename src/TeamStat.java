class TeamStat {
    String name;
    int pts = 0; // Points
    int goalsFor = 0; // Buts marqués (BM)
    int goalsAgainst = 0; // Buts encaissés (BE)
    int matches = 0; // Matchs joués (J)

    /** Constructeur */
    TeamStat(String name){
        this.name = name;
    }

    /** Calcule la différence de buts (Diff) */
    int getGoalDiff(){
        return goalsFor - goalsAgainst;
    }
}