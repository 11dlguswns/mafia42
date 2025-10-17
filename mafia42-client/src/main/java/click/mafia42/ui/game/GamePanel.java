package click.mafia42.ui.game;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.server.*;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import click.mafia42.util.TimeUtil;
import io.netty.channel.Channel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private final Channel channel;

    private final JPanel timePanel = new JPanel(new BorderLayout());
    private final JLabel timeLabel = new JLabel("<0일째> [밤] 00:00", SwingConstants.CENTER);
    private Timer timer;

    private final JScrollPane chatPane;
    private final JTextPane chatArea = new JTextPane();

    private final JTextField chatInput = new JTextField();

    private final JPanel inputPanel = new JPanel(new BorderLayout());

    public GamePanel(Channel channel) {
        this.channel = channel;
        this.setLayout(new BorderLayout());

        setTimePanel();
        chatArea.setEditable(false);
        chatPane = new JScrollPane(chatArea);
        setInputPanel();

        this.add(timePanel, BorderLayout.NORTH);
        this.add(chatPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    private void setTimePanel() {
        JButton timeDownButton = new JButton("시간 단축");
        timeDownButton.addActionListener(this::timeDown);

        JButton timeUpButton = new JButton("시간 증가");
        timeUpButton.addActionListener(this::timeUp);

        timePanel.add(timeDownButton, BorderLayout.WEST);
        timePanel.add(timeLabel, BorderLayout.CENTER);
        timePanel.add(timeUpButton, BorderLayout.EAST);
    }

    public synchronized void startTimePanel() {
        if (timer == null) {
            timer = new Timer(1000, e -> {
                updateTimePanel();
                if (DetailGameRoomProvider.detailGameRoom == null) {
                    ((Timer)e.getSource()).stop();
                }

                if (DetailGameRoomProvider.detailGameRoom != null && TimeUtil.isTimeOver(DetailGameRoomProvider.detailGameRoom.endTimeSecond())) {
                    Payload payload = new Payload(Commend.UPDATE_GAME_STATUS, new UpdateGameStatusReq());
                    Mafia42Client.sendRequest(channel, payload);

                    ((Timer)e.getSource()).stop();
                }
            });
        }
        timer.restart();
    }

    private void updateTimePanel() {
        if (DetailGameRoomProvider.detailGameRoom == null || DetailGameRoomProvider.detailGameRoom.gameStatus() == null) {
            return;
        }

        long remainingSecond = TimeUtil.getRemainingTime(DetailGameRoomProvider.detailGameRoom.endTimeSecond());
        int day = DetailGameRoomProvider.detailGameRoom.day();
        String gameStatusAlias = DetailGameRoomProvider.detailGameRoom.gameStatus().getAlias();
        long minute = remainingSecond / 60;
        long second = remainingSecond % 60;
        timeLabel.setText(String.format("<%d일째> [%s] %02d:%02d", day, gameStatusAlias, minute, second));
    }

    private void timeDown(ActionEvent e) {
        Payload payload = new Payload(Commend.DECREASE_GAME_TIME, new DecreaseGameTimeReq());
        Mafia42Client.sendRequest(channel, payload);
    }

    private void timeUp(ActionEvent e) {
        Payload payload = new Payload(Commend.INCREASE_GAME_TIME, new IncreaseGameTimeReq());
        Mafia42Client.sendRequest(channel, payload);
    }

    private void setInputPanel() {
        chatInput.addActionListener(this::sendMessage);
        JButton sendButton = new JButton("전송");
        sendButton.addActionListener(this::sendMessage);

        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
    }

    private void sendMessage(ActionEvent e) {
        String message = chatInput.getText();
        if (!message.isBlank()) {
            Payload payload = new Payload(
                    Commend.SEND_MESSAGE_TO_GAME,
                    new SendMessageToGameReq(message));
            Mafia42Client.sendRequest(channel, payload);
            chatInput.setText("");
        }
    }

    public void chatAreaAppendText(String text, Color color) {
        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("colorStyle", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), text + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
