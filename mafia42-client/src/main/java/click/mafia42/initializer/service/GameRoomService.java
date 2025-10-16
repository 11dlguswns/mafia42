package click.mafia42.initializer.service;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.client.*;
import click.mafia42.dto.server.FetchGameRoomsReq;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import click.mafia42.ui.ClientPage;
import click.mafia42.ui.ClientUI;
import click.mafia42.ui.GameRoomRole;
import io.netty.channel.ChannelHandlerContext;

import java.awt.*;

public class GameRoomService {
    private final ClientUI clientUI = ClientUI.getInstance();

    public void saveGameRoom(SaveDetailGameRoomReq request) {
        DetailGameRoomProvider.detailGameRoom = request;
        clientUI.getGamePanel().startTimePanel();

        if (request.isStarted()) {
            clientUI.setCardLayout(ClientPage.GAME);

            clientUI.getGameSubPanel().updateGameLobbyUserChoicePanel();

            if (request.gameStatus() == GameStatus.JUDGEMENT) {
                clientUI.getGameSubPanel().showJudgmentVoteDialog();
            }
        } else {
            clientUI.setCardLayout(ClientPage.GAME_ROOM_LOBBY);

            clientUI.getGameLobbySubPanel().updateGameLobbyUserChoicePanel();
            if (DetailGameRoomProvider.isCurrentUserManager()) {
                clientUI.getGameLobbySubPanel().setGameRoomRoleCardLayout(GameRoomRole.MANAGER);
            } else {
                clientUI.getGameLobbySubPanel().setGameRoomRoleCardLayout(GameRoomRole.USER);
            }
        }

    }

    public void saveGameRoomList(SaveGameRoomListReq request) {
        clientUI.getLobbyPanel().updateLobbyPanel(request);
    }

    public void removeGameRoom(RemoveGameRoomReq request, ChannelHandlerContext ctx) {
        DetailGameRoomProvider.detailGameRoom = null;
        clientUI.getGameLobbyPanel().clearChatArea();
        clientUI.setCardLayout(ClientPage.LOBBY);

        Payload payload = new Payload(
                Commend.FETCH_GAME_ROOMS,
                new FetchGameRoomsReq());
        Mafia42Client.SYNC_EXECUTOR.submit(() -> Mafia42Client.sendRequest(ctx.channel(), payload));
    }

    public void saveGameRoomLobbyMessage(SaveGameRoomLobbyMessageReq request) {
        if (DetailGameRoomProvider.detailGameRoom == null) {
            throw new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM);
        }

        SaveGameRoomUserReq gameRoomUser = DetailGameRoomProvider.detailGameRoom.getGameRoomUser(request.userId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        clientUI.getGameLobbyPanel().chatAreaAppendText(gameRoomUser.name() + " | " + request.message(), Color.BLACK);
    }

    public void saveGameRoomLobbySystemMessage(SaveGameRoomLobbySystemMessageReq request) {
        if (DetailGameRoomProvider.detailGameRoom == null) {
            throw new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM);
        }

        clientUI.getGameLobbyPanel().chatAreaAppendText("< " + request.message() + " >", Color.DARK_GRAY);
    }

    public void saveGameMessage(SaveGameMessageReq request) {
        if (DetailGameRoomProvider.detailGameRoom == null) {
            throw new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM);
        }

        SaveGameRoomUserReq gameRoomUser = DetailGameRoomProvider.detailGameRoom.getGameRoomUser(request.userId())
                .orElseThrow(() -> new GlobalException(GlobalExceptionCode.NOT_JOIN_ROOM));

        clientUI.getGamePanel().chatAreaAppendText(
                String.format("[%s] %s | %s",
                        gameRoomUser.fetchJobAlias(),
                        gameRoomUser.name(),
                        request.message()), request.messageType().getColor());
    }
}
