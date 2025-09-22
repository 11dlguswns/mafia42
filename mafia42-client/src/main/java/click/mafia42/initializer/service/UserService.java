package click.mafia42.initializer.service;

import click.mafia42.dto.client.SaveUserInfoMyselfReq;
import click.mafia42.initializer.provider.UserInfoProvider;

public class UserService {
    public void saveUserInfoMyself(SaveUserInfoMyselfReq request) {
        UserInfoProvider.nickname = request.nickname();
    }
}
