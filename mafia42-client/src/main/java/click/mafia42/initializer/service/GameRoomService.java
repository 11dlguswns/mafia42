package click.mafia42.initializer.service;

import click.mafia42.dto.SaveGameRoomReq;
import click.mafia42.initializer.provider.GameRoomProvider;

public class GameRoomService {
    public void saveGameRoom(SaveGameRoomReq request) {
        GameRoomProvider.gameRoomId = request.id();
        GameRoomProvider.gameRoomName = request.name();
        GameRoomProvider.gameRoomMaxPlayers = request.maxPlayers();
        GameRoomProvider.gameRoomPlayersCount = request.playersCount();
        GameRoomProvider.gameRoomManager = request.manager();
        GameRoomProvider.gameType = request.gameType();
    }
}
