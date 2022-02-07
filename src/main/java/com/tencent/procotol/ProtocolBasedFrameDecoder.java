package com.tencent.procotol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ProtocolBasedFrameDecoder extends LengthFieldBasedFrameDecoder{

    public ProtocolBasedFrameDecoder(){
        this(1024, 12, 4, 0, 0);
    }

    public ProtocolBasedFrameDecoder(int maxFrameLength,
                                     int lengthFieldOffset, int lengthFieldLength,
                                     int lengthAdjustment, int initialBytesToStrip){
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
