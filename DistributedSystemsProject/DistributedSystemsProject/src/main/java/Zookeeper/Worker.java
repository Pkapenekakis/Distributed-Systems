package Zookeeper;

import Exceptions.ExecutorError;
import Swarm.SwarmInitExecutor;
import Swarm.SwarmJoinExecutor;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

import static Utils.Utils.extractIntegers;


public class Worker implements Watcher {
    String hostPort;
    ZooKeeper zk;
    String znodePath = "/workers/worker-";
    int workerID; //TODO check when deleting workers what to do with id
    //String workerData;
    String JobPath;
    List<String> MAPPERS=null;

    List<String> REDUCERS = null;

    /**
     * Constructor for Worker
     * @param hostPort
     * @param workerID
     */
    public Worker(String hostPort, int workerID) {
        this.hostPort = hostPort;
        this.workerID = workerID;
        this.znodePath = this.znodePath + workerID;
    }

    /**
     * Start Zookeeper Connection
     * @throws IOException
     */
    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 1000, this);
    }


    /**
     * Create Worker znode
     */
    public void createWorker() {
        try {
            zk.create(znodePath, "00".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            zk.addWatch(znodePath, this, AddWatchMode.PERSISTENT);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Watcher for mappers
     */
    Watcher mappersWatcher = new Watcher(){
        public void process(WatchedEvent e) {
            if(e.getType() == Event.EventType.NodeChildrenChanged) {
                assert ("/Jobs/Job"+JobPath+"/mappers").equals( e.getPath() );
                getMappers();
            }
        }
    };


    /**
     * Watcher for reducers
     */
    Watcher reducersWatcher = new Watcher(){
        public void process(WatchedEvent e) {
            if(e.getType() == Event.EventType.NodeChildrenChanged) {
                assert ("/Jobs/Job"+JobPath+"/mappers").equals( e.getPath() );
                getReducers();
            }
        }
    };

    /**
     * Get the reducers of a job
     */
    public void getReducers() {
        try {
            List<String> children= zk.getChildren(JobPath+"/reducers",
                    null);
            if(children.size() == 0){
                System.out.println("No workers registered, waiting...");
                return;
            }
            if(REDUCERS == null){
                REDUCERS = children;
                System.out.println("Reducers are: " + REDUCERS);
                return;
            }else{
                for(String reducer : REDUCERS){
                    if(!children.contains(reducer)){
                        REDUCERS.remove(reducer);
                        DistributedLock lk = new DistributedLock(zk, "/assign", "worker");
                        lk.lock();
                        int[] user_id = extractIntegers(JobPath);
                        assignWorker(JobPath,user_id[0],2);

                        lk.unlock();
                    }
                }
                for(String reducer : children){
                    if(!REDUCERS.contains(reducer)){
                        REDUCERS.add(reducer);
                    }
                }
                SwarmJoinExecutor exec = new SwarmJoinExecutor(children.size(),"reducer");
            }

        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutorError e) {
            throw new RuntimeException(e);
        }
    }



    public void getMappers() {
        try {
            List<String> children= zk.getChildren(JobPath+"/mappers",
                    mappersWatcher);
            if(children.size() == 0){
                System.out.println("No workers registered, waiting...");
                return;
            }
            if(MAPPERS == null){
                MAPPERS = children;
                System.out.println("Mappers are: " + MAPPERS);
                return;
            }else{
                for(String mapper : MAPPERS){
                    if(!children.contains(mapper)){
                        MAPPERS.remove(mapper);
                        DistributedLock lk = new DistributedLock(zk, "/assign", "worker");
                        lk.lock();
                        int[] user_id = extractIntegers(JobPath);
                        assignWorker(JobPath,user_id[0],1);

                        lk.unlock();
                    }
                }
                for(String mapper : children){
                    if(!MAPPERS.contains(mapper)){
                        MAPPERS.add(mapper);
                    }
                }
                SwarmJoinExecutor exec = new SwarmJoinExecutor(children.size(),"mapper");
            }

    } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutorError e) {
            throw new RuntimeException(e);
        }
    }

    public void assignWorker(String path, int userID,int mapperOrReducerStatus) throws InterruptedException, KeeperException {
        List<String> children = null;
        children = zk.getChildren("/workers", false);
        if (children.size() == 0) {
            System.out.println("No workers available");
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            byte[] temp = zk.getData("/workers/" + children.get(i), false, null);
            String data = new String(temp);
            System.out.println(data);
            if (data.equals("00")) {
                String dataMaster=mapperOrReducerStatus+"1";
                zk.setData("/workers/" + children.get(i), dataMaster.getBytes(), -1);

                if(mapperOrReducerStatus==1){
                    zk.create(JobPath+"/mappers", new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }else{
                    zk.create(JobPath+"/reducers", new byte[0],
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }

                }

                break;
            }



        }

    Watcher JobWatcher = new Watcher(){
        public void process(WatchedEvent e) {
            if(e.getType() == Event.EventType.NodeChildrenChanged) {
                byte[] data= new byte[0];
                try {
                    data = zk.getData(JobPath, false, null);

                String dataMaster = new String(data);
                if(dataMaster.equals("Completed")){
                    deleteNodeAndChildren( zk,  JobPath );
                }
                } catch (KeeperException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    };
    private static void deleteNodeAndChildren(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(path, false);

        if (stat != null) {
            // Get the children of the node
            for (String child : zooKeeper.getChildren(path, false)) {
                String childPath = path + "/" + child;
                deleteNodeAndChildren(zooKeeper, childPath);
            }

            // Delete the node
            zooKeeper.delete(path, -1);
            System.out.println("Deleted node: " + path);
        } else {
            System.out.println("Node does not exist: " + path);
        }
    }
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            try {
                byte[] temp = zk.getData(znodePath, false, null);
                if(temp != null){
                    String data= new String(temp);
                       if(data.contains("11")){
                           JobPath = data.substring(2).replace("/master","");
                           zk.addWatch(JobPath+"/mappers", mappersWatcher, AddWatchMode.PERSISTENT);
                           zk.addWatch(JobPath+"/reducers", reducersWatcher, AddWatchMode.PERSISTENT);
                           zk.addWatch(JobPath, JobWatcher, AddWatchMode.PERSISTENT);
                           System.out.println("Master connection is active");

                           SwarmInitExecutor exec = new SwarmInitExecutor(); //TODO: may need to move to monitor
                       }
                }
            } catch (KeeperException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutorError e) {
                throw new RuntimeException(e);
            }
        }
    }
}