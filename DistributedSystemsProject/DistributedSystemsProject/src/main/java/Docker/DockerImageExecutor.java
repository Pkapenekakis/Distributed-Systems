package Docker;

import Exceptions.ExecutorError;
import Utils.CustomExecutor;

public class DockerImageExecutor extends CustomExecutor {

    /**
     * Inside the Constructor build a new docker image named map_reduce
     * @throws ExecutorError
     */
    public void execute() throws ExecutorError{
        DockerImageExecutor dockerImageExecutor = new DockerImageExecutor();
        //builds the docker image using the current directory assigning the name map_reduce to the image
        String command ="docker build . -t map_reduce";

        try{
           dockerImageExecutor.execute(command);
        }catch(Exception e){
            throw new ExecutorError("Docker image building failed");
        }

        if(dockerImageExecutor.getExitCode() !=0 ){
            throw new ExecutorError("Docker image building failed");
        }

        System.out.println("Docker image build successfully!");

    }

}
