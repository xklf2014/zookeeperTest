package com.story.zookeeper.lock;

import com.story.zookeeper.config.MyConf;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;

public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    ZooKeeper zk;
    String threadName;
    CountDownLatch latch = new CountDownLatch(1);
    String pathName;


    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void tryLock() {

        try {

            zk.create("/lock", threadName.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "create lock dir");

            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zk.delete(pathName,-1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void process(WatchedEvent event) {
        /*
        * 如果第一个节点释放锁了，此时只有第二个节点会收到通知
        * 如果后续某一个节点挂了，只有这个节点后面一个节点会收到通知
        * */
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "NodeDeleted");
                break;
            case NodeDataChanged:
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

    //StringCallback
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name != null) {
            System.out.println(threadName + " create " + name);
            pathName = name;
            zk.getChildren("/", false, this, "StringCallback");
        }
    }

    //Chilren CallBack
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        /*for (String cihld : children) {
            System.out.println(cihld);
        }*/
        Collections.sort(children);
        int index = children.indexOf(pathName.substring(1));
        if (index == 0){
            System.out.println(threadName+" is the first");
            try {
                zk.setData("/","setFirstDate".getBytes(),-1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }else{
            try {
                zk.exists("/"+children.get(index-1),this,this,"ddd");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

    }
}
