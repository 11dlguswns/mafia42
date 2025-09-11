package click.mafia42.initializer.service;

import click.mafia42.dto.SaveDetailGameRoomReq;
import click.mafia42.initializer.provider.GameRoomProvider;

public class GameRoomService {
    public void saveGameRoom(SaveDetailGameRoomReq request) {
        GameRoomProvider.gameRoomId = request.id();
        GameRoomProvider.gameRoomName = request.name();
        GameRoomProvider.gameRoomMaxPlayers = request.maxPlayers();
        GameRoomProvider.gameRoomPlayers = request.players();
        GameRoomProvider.gameRoomManager = request.manager();
        GameRoomProvider.gameType = request.gameType();
    }
}
