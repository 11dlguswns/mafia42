package click.mafia42.initializer.provider;

public class UserInfoProvider {
    public static String nickname;

    public static boolean existsUserInfo() {
        return nickname != null;
    }
}
