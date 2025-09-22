package click.mafia42.dto.server;

public record JoinGameRoomReq(
        long gameRoomId,
        String password
) {
}
