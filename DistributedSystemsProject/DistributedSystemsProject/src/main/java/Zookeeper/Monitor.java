package Zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

public class Monitor {
    String hostPort;
    ZooKeeper zk;


    /**
     * Constructor for Monitor
     * @param hostPort
     */
    public Monitor(String hostPort){
        this.hostPort = hostPort;
    }


    /**
     *  Start Zookeeper
     * @throws IOException
     */
    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 1000, null);
    }


    /**
     * Create Monitor
     */

    public void createMonitor(){
        try {
            zk.create("/monitor",new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Create Job
     * @param userID
     * @param numberOfMappers
     * @param numberOfReducers
     */
    public void createJob(int userID, int numberOfMappers, int numberOfReducers){
        try {

            //Creates the neccessary nodes for the job

            zk.create("/Jobs/Job-"+userID,"".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zk.create("/Jobs/Job-"+userID+"/mappers","".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zk.create("/Jobs/Job-"+userID+"/reducers","".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zk.create("/Jobs/Job-"+userID+"/master" , "".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            DistributedLock lk = new DistributedLock(zk, "/assign", "worker");

            //Gets access to write to the workers

            lk.lock();
            assignWorkers("/Jobs/Job-"+userID+"/master", 1, "Master", userID);
            assignWorkers("/Jobs/Job-"+userID, numberOfMappers, "Mapper", userID);
            assignWorkers("/Jobs/Job-"+userID, numberOfReducers, "Reducer", userID);

            //Releases the lock

            lk.unlock();
            zk.setData("/Jobs/Job-"+userID, "1".getBytes(), -1);
            //TODO: Initialize and Enter Docker Swarm
        } catch (KeeperException e) {

            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Assigns workers to a job
     * @param path
     * @param numOfWorkers
     * @param job
     * @param userID
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void assignWorkers(String path,int  numOfWorkers,String job, int userID) throws InterruptedException, KeeperException {
    List<String> children = null;
    children = zk.getChildren("/workers", false);
    if (children.size() == 0) {
        System.out.println("No workers available");
        return;
    }
    //if we want to assign a master
    if (job.equals("Master")) {
        for (int i = 0; i < children.size(); i++) {
            byte[] temp = zk.getData("/workers/" + children.get(i), false, null);
            String data = new String(temp);
            System.out.println(data);
            if (data.equals("00")) {
                String dataMaster="11"+ path;
                zk.setData("/workers/" + children.get(i), dataMaster.getBytes(), -1);
                zk.setData(path, children.get(i).getBytes(), -1);

                break;
            }

        }
        //if we want to assign a mapper
    } else if (job.equals("Mapper")) {


        int assigned = numOfWorkers;
        for (int i = 0; i < children.size(); i++) {
            byte[] temp = zk.getData("/workers/" + children.get(i), false, null);
            String data = new String(temp);
            System.out.println(data);
            if (data.equals("00")) {
                zk.setData("/workers/" + children.get(i), "21".getBytes(), -1);
                System.out.println("I am here");
                zk.create("/Jobs/Job-" + userID + "/mappers/" + children.get(i), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                assigned--;
                if (assigned == 0) {
                    break;

                }
            }

        }

        //if we want to assign a reducer

    } else if(job.equals("Reducer")) {
        int assigned = numOfWorkers;
        for (int i = 0; i < children.size(); i++) {
            byte[] temp = zk.getData("/workers/" + children.get(i), false, null);
            String data = new String(temp);
            System.out.println(data);
            if (data.equals("00")) {
                zk.setData("/workers/" + children.get(i), "21".getBytes(), -1);
                System.out.println("I am here");
                zk.create("/Jobs/Job-" + userID + "/reducers/" + children.get(i), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                assigned--;
                if (assigned == 0) {
                    break;

                }
            }

        }

        ;

    }
    }


}