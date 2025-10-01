package click.mafia42.database;

import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.user.User;
import click.mafia42.payload.Payload;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static click.mafia42.initializer.handler.AuthHandler.USER;

public class ChannelManager {
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public int getChannelsCount() {
        return channels.size();
    }

    public Map<User, Channel> findAllByUserIds(List<UUID> userIds) {
        return  channels.stream()
                .map(ch -> Map.entry(ch.attr(USER).get(), ch))
                .filter(e -> e.getKey() != null && userIds.contains(e.getKey().getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<Channel> findChannelByGameRoom(GameRoom gameRoom) {
        return channels.stream()
                .filter(ch -> {
                    User user = ch.attr(USER).get();
                    return user != null && gameRoom.containsPlayer(user);
                })
                .toList();
    }

    public void sendCommendToUsers(List<Channel> channels, Payload payload) {
        channels.forEach(ch -> ch.writeAndFlush(payload));
    }

    public void sendCommendToUser(User user, Payload payload) {
        channels.stream()
                .filter(ch -> user.equals(ch.attr(USER).get()))
                .findFirst()
                .ifPresent(ch -> ch.writeAndFlush(payload));
    }
}
