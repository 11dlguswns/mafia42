package click.mafia42.dto;

public record JoinGameRoomReq(
        long gameRoomId,
        String password
) {
}
