package click.mafia42.payload;

import click.mafia42.util.MapperUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@Sharable
public class PayloadDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        Payload payload = MapperUtil.readValueOrThrow(msg, Payload.class);
        out.add(payload);
    }
}
