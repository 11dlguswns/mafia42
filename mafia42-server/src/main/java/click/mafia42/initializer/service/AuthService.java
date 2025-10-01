package click.mafia42.initializer.service;

import click.mafia42.database.transaction.TransactionManager;
import click.mafia42.dto.client.SaveTokenReq;
import click.mafia42.dto.server.ReissueTokenReq;
import click.mafia42.dto.server.SignInReq;
import click.mafia42.dto.server.SignUpReq;
import click.mafia42.entity.user.User;
import click.mafia42.database.dao.UserDao;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.payload.Payload;
import click.mafia42.security.service.JwtService;
import click.mafia42.security.service.dto.TokenResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static click.mafia42.payload.Commend.*;

public class AuthService {
    private final UserDao userDao = new UserDao();
    private final JwtService jwtService = new JwtService();

    public Payload signUp(SignUpReq request) {
        return TransactionManager.executeInTransaction(connection -> {
            String hashpw = BCrypt.hashpw(request.password(), BCrypt.gensalt());

            if (userDao.findByNickname(request.nickname()).isPresent()) {
                throw new GlobalException(GlobalExceptionCode.USER_ALREADY_EXISTS);
            }

            User user = new User(request.nickname(), hashpw);
            userDao.save(user);

            TokenResponse tokenResponse = jwtService.generateTokenResponse(user);
            SaveTokenReq saveTokenReq = new SaveTokenReq(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.accessTokenExpiresIn(),
                    tokenResponse.refreshTokenExpiresIn());

            return new Payload(SAVE_TOKEN, saveTokenReq);
        });
    }

    public Payload signIn(SignInReq request) {
        return TransactionManager.executeInTransaction(connection -> {
            Optional<User> optionalUser = userDao.findByNickname(request.nickname());

            if (optionalUser.isEmpty()) {
                throw new GlobalException(GlobalExceptionCode.NOT_FOUND_USER);
            }

            if (!BCrypt.checkpw(request.password(), optionalUser.get().getPassword())) {
                throw new GlobalException(GlobalExceptionCode.PASSWORD_MISMATCH);
            }

            TokenResponse tokenResponse = jwtService.generateTokenResponse(optionalUser.get());
            SaveTokenReq saveTokenReq = new SaveTokenReq(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.accessTokenExpiresIn(),
                    tokenResponse.refreshTokenExpiresIn());

            return new Payload(SAVE_TOKEN, saveTokenReq);
        });
    }

    public Payload reissueToken(ReissueTokenReq request) {
        return TransactionManager.executeInTransaction(connection -> {
            String nickname = jwtService.getNicknameFromRefresh(request.refreshToken());


            Optional<User> optionalUser = userDao.findByNickname(nickname);

            if (optionalUser.isEmpty()) {
                throw new GlobalException(GlobalExceptionCode.NOT_FOUND_USER);
            }

            TokenResponse tokenResponse = jwtService.generateTokenResponse(optionalUser.get());
            SaveTokenReq saveTokenReq = new SaveTokenReq(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.accessTokenExpiresIn(),
                    tokenResponse.refreshTokenExpiresIn());

            return new Payload(SAVE_TOKEN, saveTokenReq);
        });
    }
}
