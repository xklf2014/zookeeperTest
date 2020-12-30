package com.story.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StatCallback,AsyncCallback.DataCallback {
    ZooKeeper zk;
    MyConf conf;
    CountDownLatch latch = new CountDownLatch(1);

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public MyConf getConf() {
        return conf;
    }

    public void setConf(MyConf conf) {
        this.conf = conf;
    }

    @Override
    public void processResult(int rc, String path, Object o, byte[] data, Stat stat) {
        if (data != null){
            String s = new String(data);
            conf.setConf(s);
            latch.countDown();
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null){
            zk.getData("/AppConf",this,this,"processResult");
        }
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/AppConf",this,this,"NodeCreated");
                break;
            case NodeDeleted:
                conf.setConf("");
                latch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/AppConf",this,this,"process");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }

    public void aWait(){
        zk.exists("/AppConf", this, this,"abc");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
