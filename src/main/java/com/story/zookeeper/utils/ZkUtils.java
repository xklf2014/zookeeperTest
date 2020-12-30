package com.story.zookeeper.utils;

import com.story.zookeeper.config.DefaultWatch;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkUtils {

    private static ZooKeeper zk;

    private static String zkAddr = "localhost:2181";

    private static DefaultWatch watch = new DefaultWatch();

    private static CountDownLatch latch = new CountDownLatch(1);

    public static ZooKeeper getZk(){
        try {
            zk = new ZooKeeper(zkAddr+"/testConf",1000,watch);
            watch.setLatch(latch);
            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        return zk;
    }

    public static ZooKeeper getZk(String path){
        try {
            zk = new ZooKeeper(zkAddr+path,1000,watch);
            watch.setLatch(latch);
            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        return zk;
    }
}
