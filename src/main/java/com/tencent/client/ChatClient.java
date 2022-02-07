package com.tencent.client;

import com.tencent.message.*;
import com.tencent.procotol.MessageCodecSharable;
import com.tencent.procotol.ProtocolBasedFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j(topic = "h.ChatClient")
public class ChatClient{

    public static void main(String[] args){

        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler  LOGGINNG_HANDLER = new LoggingHandler();
        MessageCodecSharable MESSAGECODEC_HANDLER = new MessageCodecSharable();
        // 倒计时锁
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        // 登录成功与否标志
        AtomicBoolean LOGIN = new AtomicBoolean(false);

        try {

            ChannelFuture channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception{
                            ch.pipeline().addLast(new ProtocolBasedFrameDecoder());
                            ch.pipeline().addLast(LOGGINNG_HANDLER);
                            ch.pipeline().addLast(MESSAGECODEC_HANDLER);

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception{
                                    // 客户端已建立链接, 用户在控制台输入信息
                                    new Thread(() -> {
                                        Scanner scanner = new Scanner(System.in);
                                        System.out.println("请输入用户名：");
                                        String username = scanner.nextLine();
                                        System.out.println("请输入密码：");
                                        String password = scanner.nextLine();

                                        // 构造登录请求消息对象
                                        LoginRequestMessage message = new LoginRequestMessage(username, password);
                                        // 将登录请求发送给客户端
                                        ctx.writeAndFlush(message);
                                        System.out.println("等待后续操作...");

                                        try {
                                            // 阻塞线程，等待服务器响应，看是否登陆成功
                                            WAIT_FOR_LOGIN.await();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        // 登录失败
                                        if(!LOGIN.get()){
                                            // 将客户端关闭掉
                                            ctx.channel().close();
                                            return;
                                        }


                                        while (true) {
                                            System.out.println("==================================");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gsend [group name] [content]");
                                            System.out.println("gcreate [group name] [m1,m2,m3...]");
                                            System.out.println("gmembers [group name]");
                                            System.out.println("gjoin [group name]");
                                            System.out.println("gquit [group name]");
                                            System.out.println("quit");
                                            System.out.println("==================================");

                                            String command = scanner.nextLine();
                                            // 将command以空格分开
                                            String[] s = command.split(" ");
                                            switch (s[0]){

                                                case "send":
                                                    // send [username] [content] => 给username发送内容为content的信息
                                                    String _username = s[1];
                                                    String _content = s[2];
                                                    ctx.writeAndFlush(new ChatRequestMessage(username, _username, _content));
                                                    break;
                                                case "gsend":
                                                    String _group_name = s[1];
                                                    String _group_content = s[2];
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(username, _group_name, _group_content));
                                                    break;
                                                case "gcreate":
                                                    String group_name = s[1];
                                                    String[] mem = s[2].split(",");
                                                    Set<String> members = new HashSet<>(Arrays.asList(mem));
                                                    members.add(username);
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(group_name, members));
                                                    break;
                                                case "gmembers":
                                                    String __group_name = s[1];
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(__group_name));
                                                    break;
                                                case "gjoin":
                                                    String _groupName = s[1];
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(username, _groupName));
                                                    break;
                                                case  "gquit":
                                                    String groupName = s[1];
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(username, groupName));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    return;
                                            }
                                        }

                                    }, "input").start();

                                    super.channelActive(ctx);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
                                    log.debug("msg: {}", msg);
                                    if(msg instanceof LoginResponseMessage){
                                        // 客户端收到登录响应信息
                                        LoginResponseMessage response = (LoginResponseMessage) msg;
                                        if(response.isSuccess()){
                                            // 登录成功
                                            LOGIN.set(true);
                                        }
                                        // 唤醒input线程
                                        WAIT_FOR_LOGIN.countDown();
                                    }

                                    super.channelRead(ctx, msg);
                                }


                            });

                        }
                    }).connect("localhost", 8080);
            Channel channel = channelFuture.sync().channel();
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }

    }
}
