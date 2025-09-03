package click.mafia42.initializer.handler;

import click.mafia42.payload.Payload;
import click.mafia42.initializer.service.OutputService;
import click.mafia42.util.ValidationUtil;
import click.mafia42.initializer.dto.ConsoleOutputReq;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class CommendHandler extends SimpleChannelInboundHandler<Payload> {
    private static final Logger log = LoggerFactory.getLogger(CommendHandler.class);
    private final OutputService outputService = new OutputService();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Payload payload) throws Exception {
        switch (payload.getCommend()) {
            case CONSOLE_OUTPUT
                    -> outputService.output(ValidationUtil.validationAndGet(payload.getBody(), ConsoleOutputReq.class));
            default -> log.info("서버로 부터 처리할 수 없는 커멘드 요청이 왔습니다.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
