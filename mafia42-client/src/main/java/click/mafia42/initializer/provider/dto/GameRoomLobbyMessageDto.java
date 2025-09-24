package click.mafia42.initializer.provider.dto;

public record GameRoomLobbyMessageDto(
        MessageType type,
        String nickname,
        String message
) {
    public GameRoomLobbyMessageDto(MessageType type, String nickname, String message) {
        this.type = type;
        this.message = message;

        if (type == MessageType.SYSTEM) {
            this.nickname = "SYSTEM";
        } else {
            this.nickname = nickname;
        }
    }
}
