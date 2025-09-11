package click.mafia42.initializer.service;

import click.mafia42.dto.SaveDetailGameRoomReq;
import click.mafia42.dto.SaveGameRoomListReq;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.initializer.provider.GameRoomListProvider;

public class GameRoomService {
    public void saveGameRoom(SaveDetailGameRoomReq request) {
        DetailGameRoomProvider.detailGameRoom = request;
    }

    public void saveGameRoomList(SaveGameRoomListReq request) {
        GameRoomListProvider.gameRooms = request.gameRooms();
    }
}
