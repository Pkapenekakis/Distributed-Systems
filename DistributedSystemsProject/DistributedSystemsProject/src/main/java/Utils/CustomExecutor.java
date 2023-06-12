package Utils;

import Exceptions.ExecutorError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;


public class CustomExecutor implements Executor {

    int exitCode;
    public JsonParser jp;
    public CustomExecutor(){
         jp = new JsonParser();
    }

    /**
     *Function responsible for executing the command provided.
     * The variable exitCode is used to store the exit code of the command executed,
     * 0 meaning successful termination
     * @param cmd the command to run
     * @return
     * @throws ExecutorError
     */
    public void execute(String cmd) throws ExecutorError, IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        //sh -c runs the command specified by the var cmd as (a string) in the shell
        pb.command("sh","-c",cmd);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader((process.getInputStream())));

        String line;
        while( (line = reader.readLine()) != null){
            System.out.println(line);
        }

        //Make the thread wait until the process is completed, exitCode = 0 means normal termination
        setExitCode(process.waitFor());

    }

    public int getExitCode() {
        return exitCode;
    }
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public void execute(Runnable command) {

    }
}
