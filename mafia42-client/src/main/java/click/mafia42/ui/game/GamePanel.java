package click.mafia42.ui.game;

import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private final Channel channel;

    private final JPanel timePanel = new JPanel(new BorderLayout());
    private final JLabel timeLabel = new JLabel("00:00", SwingConstants.CENTER);

    private final JScrollPane chatPane;
    private final JTextArea chatArea = new JTextArea();

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

    private void updateTimePanel(int minute, int second) {
        timeLabel.setText(String.format("%02d:%02d", minute, second));
    }
    private void timeDown(ActionEvent e) {
        // TODO 시간 단축
    }

    private void timeUp(ActionEvent e) {
        // TODO 시간 증가
    }

    private void setInputPanel() {
        chatInput.addActionListener(this::sendMessage);
        JButton sendButton = new JButton("전송");
        sendButton.addActionListener(this::sendMessage);

        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
    }

    private void sendMessage(ActionEvent e) {
        // TODO 메시지 전송
    }

}
