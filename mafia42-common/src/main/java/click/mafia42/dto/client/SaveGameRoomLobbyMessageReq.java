package click.mafia42.dto.client;

public record SaveGameRoomLobbyMessageReq(
        SaveGameRoomUserReq saveGameRoomUserReq,
        String message
) {
}
