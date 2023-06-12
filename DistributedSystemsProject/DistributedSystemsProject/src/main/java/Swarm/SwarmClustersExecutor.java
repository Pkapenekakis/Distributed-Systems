package Swarm;

import Exceptions.ExecutorError;
import Utils.CustomExecutor;

public class SwarmClustersExecutor extends CustomExecutor {

    public SwarmClustersExecutor() throws ExecutorError{
        SwarmClustersExecutor swarmClustersExecutor = new SwarmClustersExecutor();
        int numOfMappers = jp.getNumOfMappers();
        int numOfReducers= jp.getNumOfReducers();
        String command = "./src/main/Swarm/ClustersIit.sh " + numOfMappers + " " + numOfReducers;
        try{
            swarmClustersExecutor.execute(command);
        }catch (Exception e){
            throw new ExecutorError("Cluster Initialization failed");
        }

        if(swarmClustersExecutor.getExitCode() !=0 ){
            throw new ExecutorError("Cluster Initialization failed");
        }
    }
}
