package click.mafia42.ui.game_lobby;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.server.SendMessageToGameRoomLobbyReq;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameLobbyPanel extends JPanel {
    private final Channel channel;
    private final JTextArea chatArea;
    private final JTextField chatInput;

    public GameLobbyPanel(Channel channel) {
        this.channel = channel;
        this.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        chatInput = new JTextField();
        chatInput.addActionListener(this::sendMessage);
        JButton sendButton = new JButton("전송");
        sendButton.addActionListener(this::sendMessage);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage(ActionEvent e) {
        String message = chatInput.getText();
        if (!message.isBlank()) {
            Payload payload = new Payload(
                    Commend.SEND_MESSAGE_TO_GAME_ROOM_LOBBY,
                    new SendMessageToGameRoomLobbyReq(message));
            Mafia42Client.sendRequest(channel, payload);
            chatInput.setText("");
        }
    }

    public void chatAreaAppendText(String text) {
        chatArea.append(text + "\n");
    }

    public void clearChatArea() {
        chatArea.setText("");
    }
}
