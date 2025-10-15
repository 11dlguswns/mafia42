package click.mafia42.entity.room;

import java.time.Duration;

public enum GameStatus {
    NIGHT("밤", Duration.ofSeconds(25)), MORNING("아침", Duration.ofSeconds(15)), VOTING("투표", Duration.ofSeconds(15)),
    CONTRADICT("반론", Duration.ofSeconds(15)), JUDGEMENT("판결", Duration.ofSeconds(5)),
    ;

    private final String alias;
    private final Duration defaultTime;

    GameStatus(String alias, Duration defaultTime) {
        this.alias = alias;
        this.defaultTime = defaultTime;
    }

    public String getAlias() {
        return alias;
    }

    public Duration getDefaultTime() {
        return defaultTime;
    }
}
