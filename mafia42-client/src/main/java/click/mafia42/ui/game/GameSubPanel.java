package click.mafia42.ui.game;

import click.mafia42.Mafia42Client;
import click.mafia42.dto.client.SaveGameRoomUserReq;
import click.mafia42.dto.server.*;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.initializer.provider.DetailGameRoomProvider;
import click.mafia42.initializer.provider.UserInfoProvider;
import click.mafia42.job.JobType;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameSubPanel extends JPanel {
    private final Channel channel;

    private final JPanel gameUserInfoPanel = new JPanel(new GridLayout(4, 1));
    private final JLabel gameUserInfoLabel = new JLabel(UserInfoProvider.nickname, SwingConstants.CENTER);
    private final JPanel gameUserChoiceTopPanel = new JPanel(new GridLayout(1, 4));
    private final JPanel gameUserChoiceMiddlePanel = new JPanel(new GridLayout(1, 4));
    private final JPanel gameUserChoiceBottomPanel = new JPanel(new GridLayout(1, 4));

    private final JPanel gameButtonPanel = new JPanel(new GridLayout(2, 1));

    private UUID choiceUserId;
    private JobType choiceJobType;

    public GameSubPanel(Channel channel) {
        this.channel = channel;
        this.setLayout(new GridLayout(2, 1));

        setGameLobbyUserInfoPanel();
        setGameButtonPanel();

        this.add(gameUserInfoPanel);
        this.add(gameButtonPanel);
    }

    private void setGameLobbyUserInfoPanel() {
        setGameUserChoicePanel();

        gameUserInfoPanel.add(gameUserInfoLabel);
        gameUserInfoPanel.add(gameUserChoiceTopPanel);
        gameUserInfoPanel.add(gameUserChoiceMiddlePanel);
        gameUserInfoPanel.add(gameUserChoiceBottomPanel);
    }

    public void updateGameUserInfoPanel() {
        gameUserInfoLabel.setText(UserInfoProvider.nickname);
    }

    private void setGameUserChoicePanel() {
        for (int i = 1; i <= 12; i++) {
            JButton userChoiceButton = new JButton();

            userChoiceButton.setVisible(false);
            userChoiceButton.setName(String.valueOf(i));
            userChoiceButton.setBackground(Color.WHITE);
            userChoiceButton.addActionListener(e -> {
                choiceUserId = UUID.fromString(e.getActionCommand());
            });

            if (i <= 4) {
                gameUserChoiceTopPanel.add(userChoiceButton);
            } else if (i <= 8) {
                gameUserChoiceMiddlePanel.add(userChoiceButton);
            } else {
                gameUserChoiceBottomPanel.add(userChoiceButton);
            }
        }
    }

    public void updateGameLobbyUserChoicePanel() {
        java.util.List<JPanel> panels = java.util.List.of(
                gameUserChoiceTopPanel,
                gameUserChoiceMiddlePanel,
                gameUserChoiceBottomPanel);
        List<Component> components = panels.stream()
                .flatMap(panel -> Arrays.stream(panel.getComponents()))
                .toList();
        for (Component comp : components) {
            for (SaveGameRoomUserReq user : DetailGameRoomProvider.detailGameRoom.users()) {
                if (comp instanceof JButton jButton) {
                    int buttonNumber = Integer.parseInt(jButton.getName());
                    if (user.number() == buttonNumber) {
                        String jobAlias = user.fetchJobAlias();

                        StringBuilder nameArea = new StringBuilder(user.name());
                        for (SaveGameRoomUserReq gUser : DetailGameRoomProvider.detailGameRoom.users()) {
                            if (user.id().equals(gUser.targetId())) {
                                nameArea.insert(0, "&lt;").append("&gt;");
                            }
                        }

                        String voteCountMark = getVoteCountMark(user.voteCount());
                        jButton.setText(
                                "<html><div style='text-align: center;'>" +
                                        nameArea + "<br>" +
                                        jobAlias + "<br>" +
                                        voteCountMark +
                                        "</div></html>"
                        );

                        jButton.setActionCommand(user.id().toString());

                        if (user.gameUserStatus() == GameUserStatus.ALIVE) {
                            jButton.setBackground(Color.WHITE);
                        } else {
                            jButton.setBackground(Color.GRAY);
                        }

                        jButton.setVisible(true);
                        break;
                    }

                    jButton.setText(null);
                    jButton.setActionCommand(null);
                    jButton.setVisible(false);
                }
            }
        }
    }

    private String getVoteCountMark(long voteCount) {
        StringBuilder voteCountMark = new StringBuilder("[ ");
        for (int i = 0; i < voteCount; i++) {
            voteCountMark.append("/");
        }
        return voteCountMark.append(" ]").toString();
    }

    private void setGameButtonPanel() {
        JButton voteButton = new JButton("투표");
        voteButton.addActionListener(this::vote);

        JButton skillButton = new JButton("능력");
        skillButton.addActionListener(this::skill);

        gameButtonPanel.add(voteButton);
        gameButtonPanel.add(skillButton);
    }

    private void vote(ActionEvent e) {
        if (choiceUserId == null) {
            JOptionPane.showMessageDialog(null, "유저를 선택해 주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payload payload = new Payload(
                Commend.VOTE_USER,
                new VoteUserReq(choiceUserId));
        Mafia42Client.sendRequest(channel, payload);

        choiceUserId = null;
    }

    private void skill(ActionEvent e) {
        if (choiceUserId == null) {
            JOptionPane.showMessageDialog(null, "유저를 선택해 주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payload payload = new Payload(
                Commend.USE_JOB_SKILL,
                new UseJobSkillReq(choiceUserId, choiceJobType));
        Mafia42Client.sendRequest(channel, payload);

        choiceUserId = null;
    }

    public void showJudgmentVoteDialog() {
        Optional<SaveGameRoomUserReq> mostVotedUser = Optional.ofNullable(DetailGameRoomProvider.detailGameRoom.mostVotedUser());
        Optional<SaveGameRoomUserReq> currentUser = DetailGameRoomProvider.detailGameRoom.getGameRoomUser(UserInfoProvider.id);
        boolean isUserInStateDied = currentUser.isPresent() && currentUser.get().gameUserStatus() == GameUserStatus.DIE;

        if (currentUser.isPresent() && currentUser.get().isBlackmailed()) {
            return;
        }

        if (mostVotedUser.isEmpty() || isUserInStateDied) {
            return;
        }

        int result = JOptionPane.showOptionDialog(
                null,
                mostVotedUser.get().name() + "님에 대한 찬반 투표",
                "찬반 투표",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"찬성", "반대"},
                null
        );
        if (result == JOptionPane.YES_OPTION) {
            Payload payload = new Payload(
                    Commend.VOTE_AGREE,
                    new VoteAgreeReq());
            Mafia42Client.sendRequest(channel, payload);
        } else if (result == JOptionPane.NO_OPTION) {
            Payload payload = new Payload(
                    Commend.VOTE_DISAGREE,
                    new VoteDisagreeReq());
            Mafia42Client.sendRequest(channel, payload);
        }
    }
}
