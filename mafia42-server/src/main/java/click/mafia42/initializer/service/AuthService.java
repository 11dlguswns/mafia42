package click.mafia42.initializer.service;

import click.mafia42.database.transaction.TransactionManager;
import click.mafia42.database.user.User;
import click.mafia42.database.user.UserDao;
import click.mafia42.dto.*;
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
                ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.USER_ALREADY_EXISTS.getMessage());
                return new Payload(null, CONSOLE_OUTPUT, body);
            }

            User user = new User(request.nickname(), hashpw);
            userDao.save(user);

            TokenResponse tokenResponse = jwtService.generateTokenResponse(user);
            SaveTokenReq saveTokenReq = new SaveTokenReq(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.accessTokenExpiresIn(),
                    tokenResponse.refreshTokenExpiresIn());

            return new Payload(null, SAVE_TOKEN, saveTokenReq);
        });
    }

    public Payload signIn(SignInReq request) {
        return TransactionManager.executeInTransaction(connection -> {
            Optional<User> optionalUser = userDao.findByNickname(request.nickname());

            if (optionalUser.isEmpty()) {
                ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.NOT_FOUND_USER.getMessage());
                return new Payload(null, CONSOLE_OUTPUT, body);
            }

            if (!BCrypt.checkpw(request.password(), optionalUser.get().getPassword())) {
                ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.PASSWORD_MISMATCH.getMessage());
                return new Payload(null, CONSOLE_OUTPUT, body);
            }

            TokenResponse tokenResponse = jwtService.generateTokenResponse(optionalUser.get());
            SaveTokenReq saveTokenReq = new SaveTokenReq(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.accessTokenExpiresIn(),
                    tokenResponse.refreshTokenExpiresIn());

            return new Payload(null, SAVE_TOKEN, saveTokenReq);
        });
    }

    public Payload reissueToken(ReissueTokenReq request) {
        return TransactionManager.executeInTransaction(connection -> {
            String nickname = jwtService.getNicknameFromRefresh(request.refreshToken());


            Optional<User> optionalUser = userDao.findByNickname(nickname);

            if (optionalUser.isEmpty()) {
                ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.NOT_FOUND_USER.getMessage());
                return new Payload(null, CONSOLE_OUTPUT, body);
            }

            TokenResponse tokenResponse = jwtService.generateTokenResponse(optionalUser.get());
            SaveTokenReq saveTokenReq = new SaveTokenReq(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.accessTokenExpiresIn(),
                    tokenResponse.refreshTokenExpiresIn());

            return new Payload(null, SAVE_TOKEN, saveTokenReq);
        });
    }
}
