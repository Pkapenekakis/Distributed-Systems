package Swarm;

import Exceptions.ExecutorError;
import Utils.CustomExecutor;


public class SwarmJoinExecutor extends CustomExecutor {

    /**
     * The constructor essentially calls the SwarmInit script providing as arguments
     * the number of mappers and reduces provided by the user
     * @throws ExecutorError
     */
    public SwarmJoinExecutor(int numOfWorkers,String mapperOrReducer) throws ExecutorError {
        SwarmJoinExecutor joinExecutor = new SwarmJoinExecutor(numOfWorkers,mapperOrReducer);
        String command = "./src/main/java/Swarm/SwarmJoin.sh " + numOfWorkers;
        try{
            joinExecutor.execute(command);
        }catch(Exception e){
            throw new ExecutorError("Docker Swarm Join Failed");
        }

        if(joinExecutor.getExitCode() !=0 ){
            throw new ExecutorError("Docker Swarm Join Failed");
        }
    }
}
