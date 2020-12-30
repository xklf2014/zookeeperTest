package com.story.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class App {
    public static void main(String[] args) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        //new zk 传入的watch是session级别，跟path和node没有关系
        ZooKeeper zk = new ZooKeeper("localhost:2181", 3000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        Event.KeeperState state = event.getState();
                        Event.EventType type = event.getType();
                        String path = event.getPath();

                        System.out.println("new zookeeper event : "+event.toString());

                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                System.out.println("connected");
                                latch.countDown();
                                break;
                            case AuthFailed:
                                break;
                            case ConnectedReadOnly:
                                break;
                            case SaslAuthenticated:
                                break;
                            case Expired:
                                break;
                            case Closed:
                                break;
                        }

                        switch (type) {
                            case None:
                                break;
                            case NodeCreated:
                                break;
                            case NodeDeleted:
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
                });

        ZooKeeper.States state = zk.getState();

        latch.await();
        switch (state) {
            case CONNECTING:
                System.out.println("zk CONNECTING");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("zk CONNECTED");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        String pathName = zk.create("/newPath", "data1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Stat stat = new Stat();
        byte[] data = zk.getData("/newPath", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("get date watch " + event.toString());
                try {
                    //注册的事件为new zookeeper时注册的watch
                    zk.getData("/newPath",true,stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);

        System.out.println(new String(data));

        Stat stat1 = zk.setData("/newPath", "new data2".getBytes(), 0);
        Stat stat2 = zk.setData("/newPath", "new data3".getBytes(), stat1.getVersion());

        System.out.println("-----asyn start-----");
        zk.getData("/newPath", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println("-----asyn call back-----");
                System.out.println(new String(bytes));
            }
        },"abc");
        System.out.println("-----asyn end-----");

        Thread.sleep(60 * 1000);
    }
}
