package click.mafia42.initializer.service;

import click.mafia42.initializer.provider.UserInfoProvider;
import click.mafia42.dto.client.SaveUserInfoMyselfReq;
import click.mafia42.ui.ClientUI;

public class UserService {
    private final ClientUI clientUI = ClientUI.getInstance();

    public void saveUserInfoMyself(SaveUserInfoMyselfReq request) {
        UserInfoProvider.id = request.id();
        UserInfoProvider.nickname = request.nickname();
        clientUI.getLobbySubPanel().updateUserInfoPanel(request.nickname());
    }
}
