package click.mafia42.initializer.service;

import click.mafia42.dto.client.SaveTokenReq;
import click.mafia42.initializer.provider.TokenProvider;

public class TokenService {
    public void saveToken(SaveTokenReq request) {
        TokenProvider.accessToken = request.accessToken();
        TokenProvider.refreshToken = request.refreshToken();
        TokenProvider.accessTokenExpiresIn = request.accessTokenExpiresIn();
        TokenProvider.refreshTokenExpiresIn = request.refreshTokenExpiresIn();
    }
}
