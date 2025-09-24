package click.mafia42.initializer.service;

import click.mafia42.dto.client.*;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.initializer.provider.GameRoomListProvider;
import click.mafia42.initializer.provider.dto.GameRoomLobbyMessageDto;
import click.mafia42.initializer.provider.dto.MessageType;

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

        GameRoomLobbyMessageDto messageDto = new GameRoomLobbyMessageDto(
                MessageType.USER,
                request.saveGameRoomUserReq().name(),
                request.message());
        DetailGameRoomProvider.gameRoomLobbyMessages.add(messageDto);
    }

    public void saveGameRoomLobbySystemMessage(SaveGameRoomLobbySystemMessageReq request) {
        if (DetailGameRoomProvider.gameRoomLobbyMessages == null || DetailGameRoomProvider.detailGameRoom == null) {
            throw new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM);
        }

        GameRoomLobbyMessageDto messageDto = new GameRoomLobbyMessageDto(
                MessageType.SYSTEM,
                null,
                request.message());
        DetailGameRoomProvider.gameRoomLobbyMessages.add(messageDto);
    }
}
