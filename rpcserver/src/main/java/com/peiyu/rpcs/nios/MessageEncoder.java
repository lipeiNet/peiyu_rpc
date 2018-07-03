package com.peiyu.rpcs.nios;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by lipei05 on 2018/6/28.
 * 解码器
 */
public class MessageEncoder extends MessageToMessageEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, List<Object> list) throws Exception {
        if (msg==null){
            return;
        }
        ByteBuf byteBuf=null;
        if (msg instanceof byte[]){
            byteBuf= Unpooled.copiedBuffer((byte[])msg);
        }else if(msg instanceof ByteBuf){
            byteBuf=(ByteBuf)msg;
        }else if (msg instanceof ByteBuffer){
            byteBuf=Unpooled.copiedBuffer((ByteBuffer)msg);
        }else {
            String str=msg.toString();
            byteBuf=Unpooled.copiedBuffer(str, Charset.forName("UTF-8"));
        }
        //数据包
        byte[] pkg=new byte[4+byteBuf.readableBytes()];
    }
}
