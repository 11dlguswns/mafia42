package click.mafia42.initializer.provider;

import click.mafia42.dto.SaveGameRoomUserReq;
import click.mafia42.entity.room.GameType;
import click.mafia42.entity.user.User;

import java.util.List;

public class GameRoomProvider {
    public static Long gameRoomId;
    public static String gameRoomName;
    public static Integer gameRoomMaxPlayers;
    public static List<SaveGameRoomUserReq> gameRoomPlayers;
    public static User gameRoomManager;
    public static GameType gameType;
}
