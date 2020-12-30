package com.story.zookeeper.lock;

import com.story.zookeeper.config.MyConf;
import com.story.zookeeper.utils.ZkUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestLock {
    ZooKeeper zk;

    @Before
    public void conn() {
        zk = ZkUtils.getZk("/testLock");
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLock() {
        for (int i = 0; i < 10; i++) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            WatchCallBack watchCallBack = new WatchCallBack();
                            watchCallBack.setZk(zk);

                            String threadName = Thread.currentThread().getName();
                            watchCallBack.setThreadName(threadName);

                            watchCallBack.tryLock();

                       /*     try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
*/
                            watchCallBack.unLock();
                        }
                    }
            ).start();
        }
        while(true){

        }
    }
}
