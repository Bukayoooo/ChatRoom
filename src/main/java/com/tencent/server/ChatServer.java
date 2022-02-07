package com.tencent.server;

import com.tencent.procotol.MessageCodecSharable;
import com.tencent.procotol.ProtocolBasedFrameDecoder;
import com.tencent.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "h.ChatServer")
public class ChatServer{

    public static void main(String[] args){

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        LoggingHandler LOGGINNG_HANDLER = new LoggingHandler();
        MessageCodecSharable MESSAGECODEC_HANDLER = new MessageCodecSharable();
        LoginRequestMessageHandler LOGIN_HANLDER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANLDER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANLDER = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        GroupMembersRequestHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();

        try {

            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception{
                            ch.pipeline().addLast(new ProtocolBasedFrameDecoder());
                            ch.pipeline().addLast(LOGGINNG_HANDLER);
                            ch.pipeline().addLast(MESSAGECODEC_HANDLER);
                            ch.pipeline().addLast(LOGIN_HANLDER);
                            // 监听聊天请求信息处理器
                            ch.pipeline().addLast(CHAT_HANLDER);
                            ch.pipeline().addLast(GROUP_CREATE_HANLDER);
                            ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                            ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                            ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                            ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                            ch.pipeline().addLast(QUIT_HANDLER);
                        }
                    }).bind(8080);

            Channel channel = channelFuture.sync().channel();
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

}
