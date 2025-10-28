package click.mafia42.job.server;

import click.mafia42.entity.room.GameRoom;

import java.util.ArrayList;
import java.util.List;

public class SkillResult {
    private List<MessageResult> messageResults;

    public SkillResult() {
    }

    public SkillResult(List<MessageResult> messageResults) {
        this.messageResults = messageResults;
    }

    public SkillResult(MessageResult messageResult) {
        this.messageResults = new ArrayList<>(List.of(messageResult));
    }

    public List<MessageResult> getMessageResults() {
        return messageResults;
    }

    public void concat(SkillResult skillResult) {
        if (skillResult == null || skillResult.isEmpty()) {
            return;
        }
        if (messageResults == null) {
            messageResults = skillResult.messageResults;
            return;
        }

        messageResults.addAll(skillResult.messageResults);
    }

    public boolean isEmpty() {
        return messageResults == null;
    }

    public boolean hasMessageToAllUser(GameRoom gameRoom) {
        if (isEmpty()) {
            return false;
        }

        return getMessageResults().stream()
                .anyMatch(messageResult ->
                        messageResult.affectedUsers().containsAll(gameRoom.getPlayers()));
    }
}
