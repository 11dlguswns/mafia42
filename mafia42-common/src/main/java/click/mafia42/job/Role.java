package click.mafia42.job;

public enum Role {
    MAFIA(Team.MAFIA), MAFIA_ASSISTANCE(click.mafia42.job.Team.MAFIA),
    CITIZEN(click.mafia42.job.Team.CITIZEN), SPECIAL_CITIZEN(click.mafia42.job.Team.CITIZEN), POLICE(click.mafia42.job.Team.CITIZEN), DOCTOR(click.mafia42.job.Team.CITIZEN),
    CULT_LEADER(click.mafia42.job.Team.CULT), CULT_ASSISTANCE(click.mafia42.job.Team.CULT);

    private final click.mafia42.job.Team team;

    Role(click.mafia42.job.Team team) {
        this.team = team;
    }

    public click.mafia42.job.Team getTeam() {
        return team;
    }
}
