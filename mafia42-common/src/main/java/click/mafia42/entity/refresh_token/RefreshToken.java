package click.mafia42.entity.refresh_token;

import click.mafia42.entity.user.User;

public class RefreshToken {
    private User user;
    private String token;

    public RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void updateToken(String refreshToken) {
        this.token = refreshToken;
    }
}
