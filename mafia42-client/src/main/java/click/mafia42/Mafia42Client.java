package click.mafia42;

import click.mafia42.dto.server.*;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.ClientSocketChannelInitializer;
import click.mafia42.initializer.handler.CommendHandler;
import click.mafia42.initializer.provider.TokenProvider;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import click.mafia42.ui.ClientUI;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static click.mafia42.payload.Commend.*;

public class Mafia42Client {
    private static final Logger log = LoggerFactory.getLogger(Mafia42Client.class);
    public static final ExecutorService SYNC_EXECUTOR = Executors.newCachedThreadPool();
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void start() {
        MultiThreadIoEventLoopGroup eventLoopGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        try {
            Bootstrap bootstrap = new Bootstrap();
            setGroupToBootstrap(bootstrap, eventLoopGroup);
            Channel channel = bootstrap.connect().sync().channel();

            Payload fetchUserInfoPayload = new Payload(
                    FETCH_USER_INFO_MYSELF,
                    new FetchUserInfoMyselfReq()
            );
            sendRequest(channel, fetchUserInfoPayload);

            Payload fetchGameRoomsPayload = new Payload(
                    FETCH_GAME_ROOMS,
                    new FetchGameRoomsReq()
            );
            sendRequest(channel, fetchGameRoomsPayload);

            SwingUtilities.invokeLater(() -> ClientUI.getInstance(channel));
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                eventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void setGroupToBootstrap(Bootstrap bootstrap, MultiThreadIoEventLoopGroup eventLoopGroup) {
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress("localhost", 8080)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .option(ChannelOption.SO_LINGER, 0)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ClientSocketChannelInitializer());
    }

    public static void sendRequest(Channel channel, Payload payload) {
        if (isAccessTokenExpired()) {
            try {
                Payload tokenPayload = handleExpiredAccessToken();
                tokenPayload.updatePayloadId(UUID.randomUUID());
                sendRequestSync(channel, tokenPayload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        payload.updatePayloadId(UUID.randomUUID());
        payload.updateToken(TokenProvider.accessToken);

        Commend commend = payload.getCommend();
        if (commend.isSyncReq()) {
            sendRequestSync(channel, payload);
            return;
        }

        channel.writeAndFlush(payload);
    }

    private static void sendRequestSync(Channel channel, Payload payload) {
        try {
            CompletableFuture<Payload> future = new CompletableFuture<>();

            channel.pipeline().get(CommendHandler.class).setPayloadFuture(payload.getPayloadId(), future);

            if (payload.getCommend() == DISCONNECT) {
                channel.writeAndFlush(payload)
                        .addListener(ChannelFutureListener.CLOSE)
                        .sync();
                System.exit(0);
                return;
            }

            channel.writeAndFlush(payload);

            future.get();
        } catch (Exception e) {
            log.error("{}", payload, new GlobalException(GlobalExceptionCode.SYNC_PROCESS_EXCEPTION, e));
        }
    }

    private static boolean isAccessTokenExpired() {
        return TokenProvider.accessToken == null ||
                TokenProvider.accessTokenExpiresIn.isBefore(LocalDateTime.now());
    }

    private static boolean isRefreshTokenExpired() {
        return TokenProvider.refreshToken == null ||
                TokenProvider.refreshTokenExpiresIn.isBefore(LocalDateTime.now());
    }

    private static Payload handleExpiredAccessToken() throws IOException {
        if (isRefreshTokenExpired()) {
            return choiceSignOption();
        }
        return new Payload(REISSUE_TOKEN, new ReissueTokenReq(TokenProvider.refreshToken));
    }

    private static Payload choiceSignOption() throws IOException {
        log.warn("이미 계정이 있으신가요? (Y/N)");
        while (true) {
            String selectedSignOption = br.readLine().toLowerCase();

            switch (selectedSignOption) {
                case "y":
                    return requestSignIn();
                case "n":
                    return requestSignUp();
                default:
                    log.warn("Y 또는 N 값을 입력해주세요.");
            }
        }
    }

    private static Payload requestSignUp() throws IOException {
        log.warn("닉네임을 입력해주세요");
        String id = br.readLine();

        log.warn("비밀번호를 입력해주세요");
        String pw = br.readLine();

        return new Payload(SIGN_UP, new SignUpReq(id, pw));
    }

    private static Payload requestSignIn() throws IOException {
        log.warn("닉네임을 입력해주세요");
        String id = br.readLine();

        log.warn("비밀번호를 입력해주세요");
        String pw = br.readLine();

        return new Payload(SIGN_IN, new SignInReq(id, pw));
    }
}
