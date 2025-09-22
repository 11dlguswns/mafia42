package click.mafia42;

import click.mafia42.dto.server.ReissueTokenReq;
import click.mafia42.dto.server.SignInReq;
import click.mafia42.dto.server.SignUpReq;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.ClientSocketChannelInitializer;
import click.mafia42.initializer.handler.CommendHandler;
import click.mafia42.initializer.provider.TokenProvider;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import click.mafia42.util.MapperUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static click.mafia42.payload.Commend.*;

public class Mafia42Client {
    private static final Logger log = LoggerFactory.getLogger(Mafia42Client.class);
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void start() throws Exception {
        MultiThreadIoEventLoopGroup eventLoopGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        try {
            Bootstrap bootstrap = new Bootstrap();
            setGroupToBootstrap(bootstrap, eventLoopGroup);

            Channel channel = bootstrap.connect().sync().channel();
            while (true) {
                Payload payload = getPayload();

                if (payload == null) {
                    log.error("잘못된 형식의 입력 값 입니다.");
                    continue;
                }

                payload.updateToken(TokenProvider.accessToken);

                if (payload.getCommend() == DISCONNECT) {
                    channel.writeAndFlush(new Payload(payload.getToken(), DISCONNECT, payload.getBody()))
                            .addListener(ChannelFutureListener.CLOSE)
                            .sync();
                    break;
                }

                sendRequest(channel, payload);
            }
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    private void sendRequest(Channel channel, Payload payload) {
        Commend commend = payload.getCommend();

        if (commend == SIGN_IN || commend == SIGN_UP || commend == REISSUE_TOKEN) {
            sendRequestSync(channel, payload);
            return;
        }

        channel.writeAndFlush(payload);
    }

    private void sendRequestSync(Channel channel, Payload payload) {
        try {
            CompletableFuture<Payload> future = new CompletableFuture<>();

            channel.pipeline().get(CommendHandler.class).setPayloadFuture(future);
            channel.writeAndFlush(payload);

            future.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(GlobalExceptionCode.SYNC_PROCESS_EXCEPTION.getMessage());
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

    private Payload getPayload() throws IOException {
        if (isAccessTokenExpired()) {
            return handleExpiredAccessToken();
        }

        return readClientPayload();
    }

    private boolean isAccessTokenExpired() {
        return TokenProvider.accessToken == null ||
                TokenProvider.accessTokenExpiresIn.isBefore(LocalDateTime.now());
    }

    private boolean isRefreshTokenExpired() {
        return TokenProvider.refreshToken == null ||
                TokenProvider.refreshTokenExpiresIn.isBefore(LocalDateTime.now());
    }

    private Payload handleExpiredAccessToken() throws IOException {
        if (isRefreshTokenExpired()) {
            return choiceSignOption();
        }
        return new Payload(null, REISSUE_TOKEN, new ReissueTokenReq(TokenProvider.refreshToken));
    }

    private Payload choiceSignOption() throws IOException {
        while (true) {
            log.warn("이미 계정이 있으신가요? (Y/N)");
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

    private Payload requestSignUp() throws IOException {
        log.warn("닉네임을 입력해주세요");
        String id = br.readLine();

        log.warn("비밀번호를 입력해주세요");
        String pw = br.readLine();

        return new Payload(null, SIGN_UP, new SignUpReq(id, pw));
    }

    private Payload requestSignIn() throws IOException {
        log.warn("닉네임을 입력해주세요");
        String id = br.readLine();

        log.warn("비밀번호를 입력해주세요");
        String pw = br.readLine();

        return new Payload(null, SIGN_IN, new SignInReq(id, pw));
    }

    private Payload readClientPayload() throws IOException {
        String line = br.readLine();
        return JsonToPayload(line);
    }

    private Payload JsonToPayload(String line) {
        try {
            return MapperUtil.readValueOrThrow(line, Payload.class);
        } catch (Exception e) {
            return null;
        }
    }
}
