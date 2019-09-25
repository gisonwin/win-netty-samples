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
