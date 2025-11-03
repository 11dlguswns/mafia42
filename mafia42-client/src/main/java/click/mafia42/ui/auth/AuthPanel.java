package click.mafia42.ui.auth;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.server.*;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import click.mafia42.ui.ClientPage;
import click.mafia42.ui.ClientUI;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;

import static click.mafia42.payload.Commend.*;

public class AuthPanel extends JPanel {
    private final Channel channel;

    private final JTextField nicknameField;
    private final JPasswordField passwordField;
    private final JButton signInButton;
    private final JButton signUpButton;

    public AuthPanel(Channel channel) {
        this.channel = channel;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Mafia42 로그인", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("아이디:"), gbc);

        nicknameField = new JTextField(15);
        gbc.gridx = 1;
        add(nicknameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("비밀번호:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        signInButton = new JButton("로그인");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(signInButton, gbc);

        signUpButton = new JButton("회원가입");
        gbc.gridx = 1;
        add(signUpButton, gbc);

        signInButton.addActionListener(e -> signIn());
        signUpButton.addActionListener(e -> signUp());
    }

    private void signIn() {
        String nickname = nicknameField.getText();
        String password = new String(passwordField.getPassword());

        if (nickname.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payload payload = new Payload(
                Commend.SIGN_IN,
                new SignInReq(nickname, password));

        Mafia42Client.sendRequest(channel, payload);

        setDefaultInfo();

        nicknameField.setText("");
        passwordField.setText("");
    }

    private void signUp() {
        String nickname = nicknameField.getText();
        String password = new String(passwordField.getPassword());

        if (nickname.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payload payload = new Payload(
                Commend.SIGN_UP,
                new SignUpReq(nickname, password));
        Mafia42Client.sendRequest(channel, payload);

        nicknameField.setText("");
        passwordField.setText("");
    }

    private void setDefaultInfo() {
        Payload fetchUserInfoPayload = new Payload(
                FETCH_USER_INFO_MYSELF,
                new FetchUserInfoMyselfReq()
        );
        Mafia42Client.sendRequest(channel, fetchUserInfoPayload);

        Payload fetchGameRoomsPayload = new Payload(
                FETCH_GAME_ROOMS,
                new FetchGameRoomsReq()
        );
        Mafia42Client.sendRequest(channel, fetchGameRoomsPayload);

        Payload fetchJoinedGameRoom = new Payload(
                FETCH_JOINED_GAME_ROOM,
                new FetchJoinedGameRoomReq()
        );
        Mafia42Client.sendRequest(channel, fetchJoinedGameRoom);

        if (DetailGameRoomProvider.detailGameRoom == null) {
            ClientUI.getInstance().setCardLayout(ClientPage.LOBBY);
        } else {
            ClientUI.getInstance().setCardLayout(ClientPage.GAME);
        }

    }
}