package click.mafia42.payload;

public enum Commend {
    NOTHING(false),

    // SERVER
    DISCONNECT(true),
    SIGN_UP(true),
    SIGN_IN(true),
    REISSUE_TOKEN(true),
    CREATE_GAME_ROOM(false),
    JOIN_GAME_ROOM(false),
    FETCH_GAME_ROOMS(true),
    FETCH_USER_INFO_MYSELF(true),
    EXIT_GAME_ROOM(true),
    SEND_MESSAGE_TO_GAME_ROOM_LOBBY(false),
    KICK_OUT_GAME_ROOM_USER(false),
    START_GAME(false),
    UPDATE_GAME_STATUS(false),
    DECREASE_GAME_TIME(false),
    INCREASE_GAME_TIME(false),
    SEND_MESSAGE_TO_GAME(false),
    VOTE_USER(false),

    // CLIENT
    CONSOLE_OUTPUT(false),
    SAVE_TOKEN(false),
    SAVE_GAME_ROOM(false),
    SAVE_GAME_ROOM_LIST(false),
    SAVE_USER_INFO_MYSELF(false),
    REMOVE_GAME_ROOM(false),
    SAVE_GAME_ROOM_LOBBY_MESSAGE(false),
    SAVE_GAME_ROOM_LOBBY_SYSTEM_MESSAGE(false),
    SAVE_GAME_MESSAGE(false),
    ;

    private final boolean isSyncReq;

    Commend(boolean isSyncReq) {
        this.isSyncReq = isSyncReq;
    }

    public boolean isSyncReq() {
        return isSyncReq;
    }
}
