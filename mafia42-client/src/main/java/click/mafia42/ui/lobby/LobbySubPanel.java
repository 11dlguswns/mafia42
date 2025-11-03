package click.mafia42.ui.lobby;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.server.CreateGameRoomReq;
import click.mafia42.dto.server.FetchGameRoomsReq;
import click.mafia42.entity.room.GameType;
import click.mafia42.initializer.provider.UserInfoProvider;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class LobbySubPanel extends JPanel {
    private final Channel channel;

    private JPanel userInfoPanel;
    private JPanel lobbyButtonPanel;
    private JLabel userInfoLabel;

    public LobbySubPanel(Channel channel) {
        this.channel = channel;
        this.setLayout(new GridLayout(2, 1));

        setUserInfoPanel();
        setLobbyButtenPanel();

        this.add(userInfoPanel);
        this.add(lobbyButtonPanel);
    }

    private void setLobbyButtenPanel() {
        lobbyButtonPanel = new JPanel(new GridLayout(3, 1));

        JButton createGameRoomButton = new JButton("방 생성");
        createGameRoomButton.addActionListener(this::createGameRoom);

        JButton fetchGameRoomButton = new JButton("새로고침");
        fetchGameRoomButton.addActionListener(this::fetchGameRooms);

        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(this::logout);

        lobbyButtonPanel.add(createGameRoomButton);
        lobbyButtonPanel.add(fetchGameRoomButton);
        lobbyButtonPanel.add(logoutButton);
    }

    private void createGameRoom(ActionEvent e) {
        JTextField nameField = new JTextField();
        JTextField maxUserField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel typePanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup group = new ButtonGroup();
        Map<JRadioButton, GameType> radioMap = new LinkedHashMap<>();

        boolean first = true;
        //for (GameType type : GameType.values()) {
        GameType type = GameType.CLASSIC;
            JRadioButton radio = new JRadioButton(type.name());

            if (first) {
                radio.setSelected(true);
                first = false;
            }

            group.add(radio);
            typePanel.add(radio);
            radioMap.put(radio, type);
        //}

        radioMap.keySet().iterator().next().setSelected(true);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("방 제목:"));
        panel.add(nameField);
        panel.add(new JLabel("인원 수:"));
        panel.add(maxUserField);
        panel.add(new JLabel("비밀번호:"));
        panel.add(passwordField);
        panel.add(new JLabel("게임 모드:"));
        panel.add(typePanel);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "게임방 생성",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String gameRoomName = nameField.getText().trim();
                int gameRoomMaxUser = Integer.parseInt(maxUserField.getText().trim());
                String gameRoomPassword = new String(passwordField.getPassword());

                GameType selectedType = null;
                for (Map.Entry<JRadioButton, GameType> entry : radioMap.entrySet()) {
                    if (entry.getKey().isSelected()) {
                        selectedType = entry.getValue();
                        break;
                    }
                }

                Payload payload = new Payload(
                        Commend.CREATE_GAME_ROOM,
                        new CreateGameRoomReq(gameRoomName, gameRoomMaxUser, selectedType, gameRoomPassword)
                );
                Mafia42Client.sendRequest(channel, payload);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "인원 수는 숫자로 입력하세요.");
            }
        }
    }

    private void fetchGameRooms(ActionEvent e) {
        Payload payload = new Payload(
                Commend.FETCH_GAME_ROOMS,
                new FetchGameRoomsReq());
        Mafia42Client.sendRequest(channel, payload);
    }

    private void logout(ActionEvent e) {
        Payload payload = new Payload(
                Commend.DISCONNECT,
                null);
        Mafia42Client.sendRequest(channel, payload);
    }

    private void setUserInfoPanel() {
        userInfoPanel = new JPanel(new BorderLayout());

        userInfoLabel = new JLabel("로그인이 필요합니다", SwingConstants.CENTER);

        userInfoPanel.add(userInfoLabel, BorderLayout.CENTER);
    }

    public void updateUserInfoPanel() {
        userInfoLabel.setText(UserInfoProvider.nickname);
    }
}
