package Swarm;

import Exceptions.ExecutorError;
import Utils.CustomExecutor;


public class SwarmInitExecutor extends CustomExecutor {

    /**
     * The constructor essentially calls the SwarmInit script providing as arguments
     * the number of mappers and reduces provided by the user
     * @throws ExecutorError
     */
    public SwarmInitExecutor() throws ExecutorError {
        SwarmInitExecutor initExec = new SwarmInitExecutor();
        String command = "./src/main/java/Swarm/SwarmInit.sh ";
        try{
            initExec.execute(command);
        }catch(Exception e){
            throw new ExecutorError("Docker Swarm Image Sending Failed");
        }

        if(initExec.getExitCode() !=0 ){
            throw new ExecutorError("Docker Swarm Image Sending Failed");
        }
    }
}
