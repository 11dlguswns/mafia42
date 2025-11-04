package click.mafia42.ui.game_lobby;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.client.SaveGameRoomUserReq;
import click.mafia42.dto.server.ExitGameRoomReq;
import click.mafia42.dto.server.KickOutGameRoomUserReq;
import click.mafia42.dto.server.StartGameReq;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.initializer.provider.UserInfoProvider;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import click.mafia42.ui.GameRoomRole;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameLobbySubPanel extends JPanel {
    private final Channel channel;

    private CardLayout gameRoomRoleCardLayout = new CardLayout();

    private final JPanel gameLobbyUserInfoPanel = new JPanel(new GridLayout(4, 1));
    private final JLabel gameLobbyUserInfoLabel = new JLabel(UserInfoProvider.nickname, SwingConstants.CENTER);
    private final JPanel gameLobbyUserChoiceTopPanel = new JPanel(new GridLayout(1, 4));
    private final JPanel gameLobbyUserChoiceMiddlePanel = new JPanel(new GridLayout(1, 4));
    private final JPanel gameLobbyUserChoiceBottomPanel = new JPanel(new GridLayout(1, 4));

    private final JPanel gameLobbyButtonPanel = new JPanel(gameRoomRoleCardLayout);
    private final JPanel gameLobbyUserButtonPanel= new JPanel(new GridLayout(1, 1));
    private final JPanel gameLobbyManagerButtonPanel = new JPanel(new GridLayout(3, 1));

    private UUID choiceUserId;

    public GameLobbySubPanel(Channel channel) {
        this.channel = channel;
        this.setLayout(new GridLayout(2, 1));

        setGameRoomRoleCardLayout(GameRoomRole.USER);
        setGameLobbyUserInfoPanel();
        setGameLobbyButtenPanel();

        this.add(gameLobbyUserInfoPanel);
        this.add(gameLobbyButtonPanel);
    }

    public void setGameRoomRoleCardLayout(GameRoomRole gameRoomRole) {
        if (gameLobbyButtonPanel != null) {
            gameRoomRoleCardLayout.show(gameLobbyButtonPanel, gameRoomRole.name());
        }
    }

    public void updateGameLobbyUserChoicePanel() {
        List<JPanel> panels = List.of(
                gameLobbyUserChoiceTopPanel,
                gameLobbyUserChoiceMiddlePanel,
                gameLobbyUserChoiceBottomPanel);
        List<Component> components = panels.stream()
                .flatMap(panel -> Arrays.stream(panel.getComponents()))
                .toList();
        for (Component comp : components) {
            for (SaveGameRoomUserReq user : DetailGameRoomProvider.detailGameRoom.users()) {
                if (comp instanceof JButton jButton) {
                    int buttonNumber = Integer.parseInt(jButton.getName());
                    if (user.number() == buttonNumber) {
                        jButton.setText(user.name());
                        jButton.setActionCommand(user.id().toString());

                        if (DetailGameRoomProvider.detailGameRoom.manager().equals(user)) {
                            jButton.setBackground(Color.YELLOW);
                        } else {
                            jButton.setBackground(Color.WHITE);
                        }

                        jButton.setVisible(true);
                        break;
                    }

                    jButton.setText(null);
                    jButton.setActionCommand(null);
                    jButton.setBackground(Color.WHITE);
                    jButton.setVisible(false);
                }
            }
        }
    }

    private void setGameLobbyUserInfoPanel() {
        setGameLobbyUserChoicePanel();

        gameLobbyUserInfoPanel.add(gameLobbyUserInfoLabel);
        gameLobbyUserInfoPanel.add(gameLobbyUserChoiceTopPanel);
        gameLobbyUserInfoPanel.add(gameLobbyUserChoiceMiddlePanel);
        gameLobbyUserInfoPanel.add(gameLobbyUserChoiceBottomPanel);
    }

    public void updateLobbyUserInfoPanel() {
        gameLobbyUserInfoLabel.setText(UserInfoProvider.nickname);
    }

    private void setGameLobbyUserChoicePanel() {
        for (int i = 1; i <= 12; i++) {
            JButton userChoiceButton = new JButton();

            userChoiceButton.setVisible(false);
            userChoiceButton.setName(String.valueOf(i));
            userChoiceButton.setBackground(Color.WHITE);
            userChoiceButton.addActionListener(e -> {
                choiceUserId = UUID.fromString(e.getActionCommand());
            });

            if (i <= 4) {
                gameLobbyUserChoiceTopPanel.add(userChoiceButton);
            } else if (i <= 8) {
                gameLobbyUserChoiceMiddlePanel.add(userChoiceButton);
            } else {
                gameLobbyUserChoiceBottomPanel.add(userChoiceButton);
            }
        }
    }

    private void setGameLobbyButtenPanel() {
        setGameRoomLobbyUserButtenPanel();
        setGameRoomLobbyManagerButtenPanel();

        gameLobbyButtonPanel.add(gameLobbyUserButtonPanel, GameRoomRole.USER.name());
        gameLobbyButtonPanel.add(gameLobbyManagerButtonPanel, GameRoomRole.MANAGER.name());
    }

    private void setGameRoomLobbyUserButtenPanel() {
        JButton exitGameRoomButton = new JButton("나가기");
        exitGameRoomButton.addActionListener(this::exitGameRoom);

        gameLobbyUserButtonPanel.add(exitGameRoomButton);
    }

    private void setGameRoomLobbyManagerButtenPanel() {
        JButton startGameButton = new JButton("게임 시작");
        startGameButton.addActionListener(this::startGame);

        JButton kickOutGameUser = new JButton("강제 퇴장");
        kickOutGameUser.addActionListener(this::kickOutGameRoomUser);

        JButton exitGameRoomButton = new JButton("나가기");
        exitGameRoomButton.addActionListener(this::exitGameRoom);

        gameLobbyManagerButtonPanel.add(startGameButton);
        gameLobbyManagerButtonPanel.add(kickOutGameUser);
        gameLobbyManagerButtonPanel.add(exitGameRoomButton);
    }

    private void exitGameRoom(ActionEvent e) {
        Payload payload = new Payload(
                Commend.EXIT_GAME_ROOM,
                new ExitGameRoomReq());
        Mafia42Client.sendRequest(channel, payload);
    }

    private void startGame(ActionEvent e) {
        Payload payload = new Payload(
                Commend.START_GAME,
                new StartGameReq());
        Mafia42Client.sendRequest(channel, payload);
    }

    private void kickOutGameRoomUser(ActionEvent e) {
        if (choiceUserId == null) {
            JOptionPane.showMessageDialog(null, "유저를 선택해 주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (UserInfoProvider.id.equals(choiceUserId)) {
            JOptionPane.showMessageDialog(null, "자기 자신을 강퇴할 수 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payload payload = new Payload(
                Commend.KICK_OUT_GAME_ROOM_USER,
                new KickOutGameRoomUserReq(choiceUserId));
        Mafia42Client.sendRequest(channel, payload);

        choiceUserId = null;
    }
}
