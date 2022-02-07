package com.tencent.procotol;

import com.tencent.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j(topic = "h.MessageCodecSharable")
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message>{

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception{
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        // 1. 4字节的魔数
        out.writeBytes(new byte[]{1, 9, 9, 8});
        // 2. 1字节的版本号
        out.writeByte(1);
        // 3. 1字节的系列化方式：0 for jdk, 1 for json
        out.writeByte(0);   // 使用jdk序列化方式
        // 4. 1字节的指令类型
        out.writeByte(msg.getMessageType());    // 写入指令类型id
        // 5. 4个字节的请求序号
        out.writeInt(msg.getSequenceId());     // 写入请求序号
        // 对齐填充，使得非具体内容数据占16字节
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        // 将Message对象序列化为字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 声明对象输出流:创建一个写入指定 OutputStream 的 ObjectOutputStream。此构造函数将序列化流标头写入底层流
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        // 讲对象写入对象输出流中，然后对象输出流传送给底层输出流
        oos.writeObject(msg);
        // 将字节数组输出流转换为字节数组
        byte[] bytes = bos.toByteArray();

        // 7. 写入内容的字节长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
        // 1. 解码魔数
        int magic = in.readInt();
        // 2. 解码版本号
        byte version = in.readByte();
        // 3. 解码序列化方式id
        byte serializerType = in.readByte();
        // 4. 解码指令类型id
        byte messageType = in.readByte();
        // 5. 解码请求序号id
        int sequenceId = in.readInt();
        in.readByte();
        // 6. 解码字节数组长度
        int length = in.readInt();
        // 7. 解码具体内容
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Message message = (Message) ois.readObject();
        log.debug("{}, {}, {}, {}, {}, {}", magic, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(message);
    }
}
