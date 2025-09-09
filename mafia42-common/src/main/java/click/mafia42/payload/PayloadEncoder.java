package click.mafia42.payload;

import click.mafia42.util.CharsetUtil;
import click.mafia42.util.MapperUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.util.List;

@Sharable
public class PayloadEncoder extends MessageToMessageEncoder<Payload> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Payload payload, List<Object> out) throws Exception {
        try {
            Payload convertedPayload = MapperUtil.readValueOrThrow(payload, Payload.class);
            String json = MapperUtil.objectMapper.writeValueAsString(convertedPayload);
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(json), CharsetUtil.DEFUALT_CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
