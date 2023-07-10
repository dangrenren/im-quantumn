package org.lld.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.lld.codec.entity.Message;
import org.lld.codec.utils.ByteBufToMessageUtils;

import java.util.List;

/*
    * @description: 消息解码器
    * @author:dangren
    * @date: 2023/7/9
    * @version: 1.0
 */
public class MeaasgeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        //readBytes(byte[] dst)
        //参数：dst - 目标字节数组，用于存储从ByteBuf中读取的字节数据。
        //功能：从ByteBuf中将指定长度的字节数据读取到目标字节数组dst中。
        //返回值：无。
        //注意：dst字节数组必须具有足够的容量来存储读取的字节数据，否则可能会导致IndexOutOfBoundsException异常。
        if(in.readableBytes() < 28){//数据量不够，等待下一次读取
            return;
        }
        Message message = ByteBufToMessageUtils.transition(in);
        if(message == null){
            System.out.println("message is null,解析失败");
            return;
        }
        out.add(message);
    }
}
