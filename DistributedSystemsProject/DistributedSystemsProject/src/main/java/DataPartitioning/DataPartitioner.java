package DataPartitioning;

import java.io.*;
import java.nio.file.Files;


public class DataPartitioner {

    File file = new File("data.txt");

    File root = new File("./temp");

    File dest = new File(root, "./Data/Data.txt");

    public void partitionData() {
        System.out.println(file.getAbsolutePath().toString());
        System.out.println(dest.getAbsolutePath().toString());

        dest.getParentFile().mkdirs();

        try {
            Files.copy(file.toPath(), dest.toPath());

        } catch (IOException e) {
            System.out.println("Could not copy the file \n");
        }


        try {
            int size = (int) Math.ceil(file.length() / (20 * 1024 * 1024))+1;
            /*ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("sh", "-c ", "cd ./temp/Data && split -d -C " + size + " Data.txt map && rm Data.txt");
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            System.out.println("\nSplitter Exited with error code : " + exitCode);

            if (exitCode != 0) {
                throw new IOException("Couldn't split the file"); //Throw custom  exception
            }*/


            RandomAccessFile raf = new RandomAccessFile("./temp/Data/Data.txt", "r");
            long numSplits = size; //from user input, extract it from args
            long sourceSize = raf.length();
            long bytesPerSplit = sourceSize / numSplits;
            long remainingBytes = sourceSize % numSplits;

            int maxReadBufferSize = 8 * 1024; //8KB
            for (int destIx = 1; destIx <= numSplits; destIx++) {
                BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream("./temp/Data/map." + destIx));
                if (bytesPerSplit > maxReadBufferSize) {
                    long numReads = bytesPerSplit / maxReadBufferSize;
                    long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                    for (int i = 0; i < numReads; i++) {
                        readWrite(raf, bw, maxReadBufferSize);
                    }
                    if (numRemainingRead > 0) {
                        readWrite(raf, bw, numRemainingRead);
                    }
                } else {
                    readWrite(raf, bw, bytesPerSplit);
                }
                bw.close();
            }
            if (remainingBytes > 0) {
                BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream("split." + (numSplits + 1)));
                readWrite(raf, bw, remainingBytes);
                bw.close();
            }
            raf.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found \n");
        } catch (IOException e) {
            System.out.println("Could not split the file \n");
        }


    }


    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if (val != -1) {
            bw.write(buf);
        }


    }
}