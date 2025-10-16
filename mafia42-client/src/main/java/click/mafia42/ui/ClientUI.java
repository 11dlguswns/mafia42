package click.mafia42.ui;

import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.ui.game.GamePanel;
import click.mafia42.ui.game.GameSubPanel;
import click.mafia42.ui.game_lobby.GameLobbyPanel;
import click.mafia42.ui.game_lobby.GameLobbySubPanel;
import click.mafia42.ui.lobby.LobbyPanel;
import click.mafia42.ui.lobby.LobbySubPanel;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class ClientUI {
    private static final Logger log = LoggerFactory.getLogger(ClientUI.class);
    private static ClientUI instance;

    private final JFrame frame = new JFrame("Game Client");
    private final JPanel uiPanel = new JPanel(new GridLayout(1, 2));
    private final JPanel mainPanel;
    private final JPanel subPanel;
    private final CardLayout mainCardLayout = new CardLayout();
    private final CardLayout subCardLayout = new CardLayout();

    private final LobbyPanel lobbyPanel;
    private final LobbySubPanel lobbySubPanel;

    private final GameLobbyPanel gameLobbyPanel;
    private final GameLobbySubPanel gameLobbySubPanel;

    private final GamePanel gamePanel;
    private final GameSubPanel gameSubPanel;

    public static ClientUI getInstance() {
        if (instance == null) {
            throw new GlobalException(GlobalExceptionCode.NOT_INITIALIZED_UI);
        }
        return instance;
    }

    public static ClientUI getInstance(Channel channel) {
        if (instance == null) {
            instance = new ClientUI(channel);
        }
        return instance;
    }

    private ClientUI(Channel channel) {
        mainPanel = new JPanel(mainCardLayout);
        subPanel = new JPanel(subCardLayout);

        lobbyPanel = new LobbyPanel(channel);
        lobbySubPanel = new LobbySubPanel(channel);
        gameLobbyPanel = new GameLobbyPanel(channel);
        gameLobbySubPanel = new GameLobbySubPanel(channel);
        gamePanel = new GamePanel(channel);
        gameSubPanel = new GameSubPanel(channel);

        setFrame();
        setCardLayout(ClientPage.LOBBY);
    }

    private void setFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        setUIPanel();

        frame.add(uiPanel);
        frame.setVisible(true);
    }

    private void setUIPanel() {
        mainPanel.add(lobbyPanel, ClientPage.LOBBY.name());
        subPanel.add(lobbySubPanel, ClientPage.LOBBY.name());

        mainPanel.add(gameLobbyPanel, ClientPage.GAME_ROOM_LOBBY.name());
        subPanel.add(gameLobbySubPanel, ClientPage.GAME_ROOM_LOBBY.name());

        mainPanel.add(gamePanel, ClientPage.GAME.name());
        subPanel.add(gameSubPanel, ClientPage.GAME.name());

        uiPanel.add(mainPanel);
        uiPanel.add(subPanel);
    }

    public void setCardLayout(ClientPage clientPage) {
        mainCardLayout.show(mainPanel, clientPage.name());
        subCardLayout.show(subPanel, clientPage.name());
    }

    public GameLobbySubPanel getGameLobbySubPanel() {
        return gameLobbySubPanel;
    }

    public GameLobbyPanel getGameLobbyPanel() {
        return gameLobbyPanel;
    }

    public LobbySubPanel getLobbySubPanel() {
        return lobbySubPanel;
    }

    public LobbyPanel getLobbyPanel() {
        return lobbyPanel;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public GameSubPanel getGameSubPanel() {
        return gameSubPanel;
    }
}
