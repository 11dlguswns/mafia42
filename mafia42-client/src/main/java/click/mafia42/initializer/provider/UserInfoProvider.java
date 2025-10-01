package click.mafia42.initializer.provider;

import java.util.UUID;

public class UserInfoProvider {
    public static UUID id;
    public static String nickname;

    public static boolean existsUserInfo() {
        return nickname != null;
    }
}
