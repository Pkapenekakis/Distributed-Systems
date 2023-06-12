package Zookeeper;

import java.io.IOException;

public class test {

    public static void main(String[] args) throws IOException, InterruptedException {


        Monitor monitor = new Monitor("localhost:2182");
        InitialiseZoo   initialiseZoo = new InitialiseZoo("localhost:2182");



        initialiseZoo.startZK();


        monitor.startZK();
        monitor.createMonitor();



        initialiseZoo.bootstrap();
       Thread.sleep(1000);
        for(int i=0;i<15;i++){
            Worker worker = new Worker("localhost:2182",i);
            worker.startZK();
            worker.createWorker();
        }


        monitor.createJob( 24,  4,  4);

        //Thread.sleep(1000);
        //monitor.createJob(9,4,4);

        //Thread.sleep(1000);
        //monitor.changeWorkerState("1","mapper","Working");
        //worker.changeWorkerStatus("Idle");
        while(true){
        }

    }
}
