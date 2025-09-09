package click.mafia42.security.enums;

public enum JwtRule {
    ACCESS_PREFIX("Authorization"),
    REFRESH_PREFIX("Refresh-Token");

    private final String value;

    JwtRule(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}