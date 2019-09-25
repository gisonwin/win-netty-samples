# win-netty-samples
netty 4.x samples with self protocol.
```java
    /***
     * 执行项目.
     */
    public void performTBOM(String tbomContent) {
        try {
            TBOMProtocol performTBOM = NettyUtils.createPerformTBOM(tbomContent);
            activeChannelMap.entrySet().parallelStream().filter(e -> e.getValue().isActive()).map(Map.Entry::getValue).forEach(channel -> {
                channel.writeAndFlush(performTBOM);
                log.info("send TBOM  command to channel {} @ {}  tbomcontent = {}", channel, LocalDateTime.now(), tbomContent);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
        @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        activeChannelMap.put(remoteAddress, channel);
        //将channel 保存起来复用.
        //记录客户端链接建立成功
        log.info("客户端< {} " + " >连接到本服务器[ {}" + " ]", remoteAddress, channel.localAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //channel inactive ,remove current channel
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        activeChannelMap.remove(remoteAddress);
        log.warn("客户端  < {} >  断开 连接,@ {}", remoteAddress, LocalDateTime.now());
        ctx.close();
    }
        @Override
    protected void channelRead0(ChannelHandlerContext ctx, TBOMProtocol msg) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        activeChannelMap.put(remoteAddress, channel);
        }
