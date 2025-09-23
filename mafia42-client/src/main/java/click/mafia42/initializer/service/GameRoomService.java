package click.mafia42.initializer.service;

import click.mafia42.dto.client.RemoveGameRoomReq;
import click.mafia42.dto.client.SaveDetailGameRoomReq;
import click.mafia42.dto.client.SaveGameRoomListReq;
import click.mafia42.dto.client.SaveGameRoomLobbyMessageReq;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.initializer.provider.GameRoomListProvider;

import java.util.ArrayList;

public class GameRoomService {
    public void saveGameRoom(SaveDetailGameRoomReq request) {
        DetailGameRoomProvider.detailGameRoom = request;

        if (DetailGameRoomProvider.gameRoomLobbyMessages == null) {
            DetailGameRoomProvider.gameRoomLobbyMessages = new ArrayList<>();
        }
    }

    public void saveGameRoomList(SaveGameRoomListReq request) {
        GameRoomListProvider.gameRooms = request.gameRooms();
    }

    public void removeGameRoom(RemoveGameRoomReq request) {
        DetailGameRoomProvider.detailGameRoom = null;
        DetailGameRoomProvider.gameRoomLobbyMessages = null;
        GameRoomListProvider.gameRooms = null;
    }

    public void saveGameRoomLobbyMessage(SaveGameRoomLobbyMessageReq request) {
        if (DetailGameRoomProvider.gameRoomLobbyMessages == null || DetailGameRoomProvider.detailGameRoom == null) {
            throw new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM);
        }

        DetailGameRoomProvider.gameRoomLobbyMessages.add(request);
    }
}
