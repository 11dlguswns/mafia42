package click.mafia42.payload;

public enum Commend {
    // SERVER
    DISCONNECT(CommendType.SERVER),
    SIGN_UP(CommendType.SERVER),
    SIGN_IN(CommendType.SERVER),
    REISSUE_TOKEN(CommendType.SERVER),
    CREATE_GAME_ROOM(CommendType.SERVER),
    JOIN_GAME_ROOM(CommendType.SERVER),
    FETCH_GAME_ROOMS(CommendType.SERVER),
    FETCH_USER_INFO_MYSELF(CommendType.SERVER),

    // CLIENT
    CONSOLE_OUTPUT(CommendType.CLIENT),
    SAVE_TOKEN(CommendType.CLIENT),
    SAVE_GAME_ROOM(CommendType.CLIENT),
    SAVE_GAME_ROOM_LIST(CommendType.CLIENT),
    SAVE_USER_INFO_MYSELF(CommendType.CLIENT);

    private CommendType type;

    Commend(CommendType type) {
        this.type = type;
    }

    public CommendType getType() {
        return type;
    }
}
