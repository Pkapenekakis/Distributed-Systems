package Zookeeper;


import org.apache.zookeeper.*;

import java.io.IOException;

public class InitialiseZoo implements Watcher{

    ZooKeeper zk;

    String hostPort;

    /**
     * Constructor for InitialiseZoo
     * @param hostport
     */

    public InitialiseZoo(String hostport) {
        this.hostPort = hostport;
    }

    /**
     * Metod to Initiate a Zookeeper connection
     * @throws IOException
     */
    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    /**
     * Method to create the parent nodes for the Zookeeper
     */
    public void bootstrap() {
        createParent("/users", new byte[0]);
        createParent("/workers", new byte[0]);
        createParent("/Jobs", new byte[0]);
        createParent("/assign", new byte[0]);
    }

    /**
     * Method to create a parent node
     * @param path: the path of the parent node
     * @param data: the data to be written on the Parent Node
     */
    void createParent(String path, byte[] data) {
        zk.create(path,
                data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                createParentCallback,
                data);
    }

    /**
     * Method to create a parent node
     * @param rc: the path of the parent node
     * @param path: the path of the parent node
     * @param ctx: the data to be written on the Parent Node
     * @param name: the name of the parent node
     */
    AsyncCallback.StringCallback createParentCallback = new AsyncCallback.StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    createParent(path, (byte[]) ctx);
                    break;
                case OK:
                    System.out.println("Parent created");
                    break;
                case NODEEXISTS:
                    System.out.println("Parent already registered: " + path);
                    break;
                default:
                    System.out.println("Something went wrong: ");
            }
        }
    };





    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }
}
