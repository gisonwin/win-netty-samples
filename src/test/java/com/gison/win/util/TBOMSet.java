package com.gison.win.util;

import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/9/18 15:51
 */
public class TBOMSet {
    static String tbomlist = "{\"ID\":\"00000000-0000-0000-0000-000000000010\",\n" +
            "\"Name\":\"1\",\n" +
            "\"Level\":1,\n" +
            "\"SubBom\":[\n" +
            "{\"ID\":\"00000000-0000-0000-0000-000000000002\",\n" +
            "\"Name\":\"2\",\n" +
            "\"Level\":2,\n" +
            "\"SubBom\":[\n" +
            "{\"ID\":\"00000000-0000-0000-0000-000000000000\",\n" +
            "\"Name\":\"3\",\n" +
            "\"Level\":3,\n" +
            "}]}]}";

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        Map<String, String> stringStringMap = jedis.hgetAll(NettyUtils.TBOMMAP);

//        for(Map.Entry<String,String> entry : stringStringMap){
//
//        }
//        Map<String, String> map = new HashMap<String, String>(1);
//        map.put(key, "35%");
//        String hmset = jedis.hmset(key, map);
//        System.out.println(hmset);
//        jedis.del(NettyUtils.TBOMMAP);
//        for (; ; ) {
            for (String key : stringStringMap.keySet()) {
                String hget = jedis.hget(NettyUtils.TBOMMAP, key);
                System.out.println("key==>" + key + ",progress==>" + hget);
            }
            jedis.close();
            System.out.println("========================");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

//        jedis.set("tbomlist", tbomlist);
//        Map<String, String> hmget = (Map<String, String>) jedis.hmget(NettyUtils.TBOMMAP);
//        System.out.println(hmget);
//        Long id = jedis.del("tbomlist");
//        System.out.println(id);
//        RedisPool.returnResource(jedis);
        System.exit(1);
//        long value = 8999999999999999999l;
//        value=       9223000000000000000l;
//        LocalDateTime start = LocalDateTime.now();
//        String x = NettyUtils.longValueTo32HexString(value);
//        System.out.println(x.length() + " : " + x);
//        System.out.println(NettyUtils.hexStringToLong(x));
//        LocalDateTime stop = LocalDateTime.now();
//        int nano = Duration.between(start, stop).getNano();
//        System.out.println(nano);
//        System.out.println("====================");
//        start = LocalDateTime.now();
//        x = NettyUtils.intValueTo32HexString(value);
//        System.out.println(x.length() + " : " + x);
//        System.out.println(NettyUtils.hexStringToInt(x));
//        stop = LocalDateTime.now();
//        nano = Duration.between(start, stop).getNano();
//        System.out.println(nano);

    }
}
