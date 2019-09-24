package com.gison.win.util;

import com.gison.win.codec.TBOMDecoder;
import com.gison.win.codec.TBOMEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 10:01
 */
@Slf4j
public class MyClient {
    private String host;
    private int port;
    private static int reconnectCount;
    private InetSocketAddress address;

    public MyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public MyClient(InetSocketAddress address) {
        this.address = address;
    }

    public MyClient(int port) {
        this("localhost", port);
    }

    public MyClient() {
        this(80);
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress("192.168.1.9", 9000);
        MyClient myClient = new MyClient();
        ChannelFuture connect = myClient.connect(address);
        if (null == connect || !connect.isSuccess() || !connect.channel().isActive()) {
            try {
                myClient.reconnect(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reconnect(InetSocketAddress address) throws Exception {
        new MyClient().connect(address);
        TimeUnit.MICROSECONDS.sleep(2000L);
//        if (10 == reconnectCount) {
//            log.error(reconnectCount + " count ,close it");
//            ctx.close();
//            System.exit(0);
//        }
        reconnectCount++;
        log.warn("reccont NO. " + reconnectCount + " count(s)");
    }

    /***
     * client connect to server
     */
    public ChannelFuture connect(InetSocketAddress address) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new TBOMDecoder());
                            pipeline.addLast(new TBOMEncoder());
                            //超时Handler 心跳检测
                            pipeline.addLast(new IdleStateHandler(0,0,10, TimeUnit.SECONDS));
                            pipeline.addLast(new MyClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(address).sync();
            future.channel().closeFuture().sync();
            return future;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }
}
