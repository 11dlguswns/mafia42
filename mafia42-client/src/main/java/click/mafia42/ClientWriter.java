package click.mafia42;

import click.mafia42.dto.client.SaveDetailGameRoomReq;
import click.mafia42.dto.client.SaveGameRoomReq;
import click.mafia42.dto.client.SaveGameRoomUserReq;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.initializer.provider.GameRoomListProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ClientWriter implements Runnable {
    private final Path clientFile = Path.of("mafia42-client/src/main/resources/" + UUID.randomUUID() + "Client.txt");

    @Override
    public void run() {
        try {
            if (!Files.exists(clientFile)) {
                Files.createFile(clientFile);
            }

            while (true) {
                String writeString = gameRoomWrite();
                Files.writeString(clientFile, writeString, StandardCharsets.UTF_8);
                Thread.sleep(100);
            }
        } catch (IOException e) {
            throw new GlobalException(GlobalExceptionCode.CLIENT_WRITE_FAIL);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String gameRoomWrite() throws IOException {
        if (DetailGameRoomProvider.detailGameRoom != null) {
            return detailGameRoomToString();
        } else {
            return gameRoomListToString();
        }
    }

    private String detailGameRoomToString() {
        StringBuilder writeString = new StringBuilder();

        SaveDetailGameRoomReq detailGameRoom = DetailGameRoomProvider.detailGameRoom;
        writeString.append("[ ").append(detailGameRoom.id()).append(" ] ").append(detailGameRoom.name()).append(" - ").append(detailGameRoom.gameType()).append("\n");
        for (SaveGameRoomUserReq user : detailGameRoom.users()) {
            writeString.append(user.name());

            if (detailGameRoom.manager().getId() == user.id()) {
                writeString.append(" [ 방장 ]");
            }
            writeString.append("\n");
        }

        return writeString.toString();
    }

    private String gameRoomListToString() {
        StringBuilder writeString = new StringBuilder();

        writeString.append("[ 게임 방 ]\n");
        if (GameRoomListProvider.gameRooms == null) {
            writeString.append("게임 방 전체 목록 정보가 없습니다.");
            return writeString.toString();
        }
        for (SaveGameRoomReq gameRoom : GameRoomListProvider.gameRooms) {
            writeString.append("[").append(gameRoom.id()).append("] ").append(gameRoom.name()).append(" - ").append(gameRoom.gameType()).append(" (").append(gameRoom.playersCount()).append("/").append(gameRoom.maxPlayers()).append(")\n");
        }

        return writeString.toString();
    }
}
