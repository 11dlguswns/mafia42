package click.mafia42.dto.server;

import click.mafia42.entity.room.GameType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CreateGameRoomReq(
        @Size(min = 1, max = 20, message = "방 이름은 20글자 이하여야 합니다.")
        String name,
        @Min(value = 4, message = "인원은 4명 이상이여야 합니다.")
        @Max(value = 12, message = "인원은 12명 이하여야 합니다.")
        int maxPlayers,
        GameType gameType,
        String password
) {
}
