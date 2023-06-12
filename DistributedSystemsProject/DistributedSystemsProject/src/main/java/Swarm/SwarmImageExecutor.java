package Swarm;

import Exceptions.ExecutorError;
import Utils.CustomExecutor;


public class SwarmImageExecutor extends CustomExecutor {

    /**
     * The constructor essentially calls the SwarmImageSaver script
     * @throws ExecutorError
     */
    public void execute() throws ExecutorError {
        SwarmImageExecutor imageExec = new SwarmImageExecutor();
        String command = "./src/main/java/Swarm/SwarmImageSaver.sh";
        try{
            imageExec.execute(command);
        }catch(Exception e){
            throw new ExecutorError("Docker Swarm Image Sending Failed");
        }

        if(imageExec.getExitCode() !=0 ){
            throw new ExecutorError("Docker Swarm Image Sending Failed");
        }
    }
}
