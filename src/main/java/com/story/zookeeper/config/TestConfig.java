package com.story.zookeeper.config;

import com.story.zookeeper.utils.ZkUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {
    ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZkUtils.getZk();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf() throws InterruptedException {

        WatchCallBack watch = new WatchCallBack();
        watch.setZk(zk);
        MyConf conf = new MyConf();
        watch.setConf(conf);


        watch.aWait();

        while (true){

            if ("".equals(conf.getConf())){
                System.out.println("conf missing");
                watch.aWait();
            }

            System.out.println(conf.getConf());
            Thread.sleep(1000);
        }
    }
}
