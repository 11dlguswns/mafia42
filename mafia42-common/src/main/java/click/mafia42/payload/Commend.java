package click.mafia42.payload;

public enum Commend {
    // SERVER
    DISCONNECT(CommendType.SERVER),

    // CLIENT
    CONSOLE_OUTPUT(CommendType.CLIENT);

    private CommendType type;

    Commend(CommendType type) {
        this.type = type;
    }

    public CommendType getType() {
        return type;
    }
}
