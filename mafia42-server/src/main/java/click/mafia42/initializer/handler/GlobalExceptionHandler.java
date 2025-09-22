package click.mafia42.initializer.handler;

import click.mafia42.dto.client.ConsoleOutputReq;
import click.mafia42.dto.client.ConsoleType;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);

        Payload payload = toPayloadByException(cause);
        ctx.channel().writeAndFlush(payload);
    }

    private Payload toPayloadByException(Throwable cause) {
        if (cause instanceof GlobalException globalException) {
            ConsoleOutputReq body = new ConsoleOutputReq(globalException.getCodeAndMessage(), ConsoleType.ERROR);
            return new Payload(null, Commend.CONSOLE_OUTPUT, body);
        } else {
            ConsoleOutputReq body = new ConsoleOutputReq(
                    GlobalException.getCodeAndMessage(GlobalExceptionCode.UNKNOWN_ERROR),
                    ConsoleType.ERROR);
            return new Payload(null, Commend.CONSOLE_OUTPUT, body);
        }
    }
}
