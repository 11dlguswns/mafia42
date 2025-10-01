package click.mafia42.ui.lobby;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.client.SaveGameRoomListReq;
import click.mafia42.dto.client.SaveGameRoomReq;
import click.mafia42.dto.server.JoinGameRoomReq;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LobbyPanel extends JPanel {
    private final Channel channel;

    public LobbyPanel(Channel channel) {
        this.channel = channel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void updateLobbyPanel(SaveGameRoomListReq request) {
        this.removeAll();

        request.gameRooms().forEach(gameRoom -> {
            JButton gameRoomButton = new JButton(getGameRoomAlias(gameRoom));

            gameRoomButton.setActionCommand(String.valueOf(gameRoom.id()));
            gameRoomButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            gameRoomButton.addActionListener(e -> joinGameRoom(e, channel, gameRoom.existPassword()));

            this.add(gameRoomButton);
            this.add(Box.createVerticalStrut(5));
        });

        this.revalidate();
        this.repaint();
    }

    private String getGameRoomAlias(SaveGameRoomReq gameRoom) {
        return "[" + gameRoom.id() + "] " + gameRoom.name() + " - " +
                gameRoom.gameType() + " (" + gameRoom.playersCount() + "/" + gameRoom.maxPlayers() + ")";
    }


    private void joinGameRoom(ActionEvent e, Channel channel, boolean existPassword) {
        long roomId = Long.parseLong(e.getActionCommand());
        String password = getPassword(existPassword);

        Payload payload = new Payload(
                Commend.JOIN_GAME_ROOM,
                new JoinGameRoomReq(roomId, password));
        Mafia42Client.sendRequest(channel, payload);
    }

    private String getPassword(boolean existPassword) {
        if (!existPassword) {
            return null;
        }

        return JOptionPane.showInputDialog("비밀번호를 입력하세요:");
    }
}
