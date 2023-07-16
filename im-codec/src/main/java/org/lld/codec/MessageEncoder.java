package org.lld.codec;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.lld.codec.entity.MessagePack;

/**
 * @author: Chackylee
 * @description: 消息编码类，私有协议规则，前4位表示长度，接着command4位，后面是数据
 **/
public class MessageEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof MessagePack){
            MessagePack msgBody = (MessagePack) msg;
            String s = JSONObject.toJSONString(msgBody.getData());
            byte[] bytes = s.getBytes();
            out.writeInt(msgBody.getCommand());
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }

}
