package click.mafia42.initializer.provider;

import click.mafia42.dto.client.SaveDetailGameRoomReq;

public class DetailGameRoomProvider {
    public static SaveDetailGameRoomReq detailGameRoom;

    public static boolean isCurrentUserManager() {
        String currentUserNickname = UserInfoProvider.nickname;
        return detailGameRoom.manager().name().equals(currentUserNickname);
    }
}
