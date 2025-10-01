package click.mafia42.initializer.handler;

import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message = toStringByException(cause);
        log.error(message, cause);
    }

    private String  toStringByException(Throwable cause) {
        if (cause instanceof GlobalException globalException) {
            return globalException.getCodeAndMessage();
        } else {
            return GlobalException.getCodeAndMessage(GlobalExceptionCode.UNKNOWN_ERROR);
        }
    }
}
