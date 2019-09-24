package com.gison.win.util;

import com.gison.win.server.LNSServer;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/9/10 8:52
 */
public class Test {
    public static void main(String[] args) {

        int port = 9000;
        LNSServer server = new LNSServer();
        server.setPort(port);

        ChannelFuture future = server.start();
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (Exception ex) {

        }
//        for(int i=0;i<10;i++) {
//            System.out.println("i==> "+i );
            server.performTBOM("{\"ID\":\"00000000000000000000000000000145\",\"State\":\"1\"}");
//            try {
//                TimeUnit.SECONDS.sleep(8);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        future.channel().closeFuture().syncUninterruptibly();

//        NettyUtils.setReportPath("C:\\");
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> LNSServer.stop()));
    }
}
