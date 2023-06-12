package Docker;

import Exceptions.ExecutorError;
import Utils.JsonParser;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.IllegalFormatCodePointException;
import java.util.concurrent.Executor;

public class DockerExecutor implements Executor {

    JsonParser jp = new JsonParser();

    /**
     * The function prepares the docker by calling the necessary functions to prepare the mappers
     * and the reducers
     * @throws ExecutorError
     */
    public void execute() throws ExecutorError{
        try{
            //TODO: Change the param names depending on front end
            prepareMapperCode(jp.getMapperCode(), jp.getImports());
            prepareReducerCode(jp.getReducerCode(),jp.getImports());

        }catch(Exception e){
            throw new ExecutorError("User code could not be compiled");
        }
    }


    @Override
    public void execute(Runnable command) {

    }

    /**
     * Function responsible for creating a file (MapperPrep) inside the ./temp folder holding the user code for the mapper function
     * Proceeding to run the code producing
     * @param method the method of mapping defined by the user. TODO: may need to change completely depending on front end
     * @param userImports imports defined by the user code
     * @throws IOException
     */
    public void prepareMapperCode(String method, String userImports) throws IOException {
        String source = userImports = " import java.io.*;\n" +
                " public class MapperUtil { " + method + " }";

        File root = new File("./temp");
        File sourceFile = new File(root, "mapper/MapperPrep.java");
        //makes sure the parent directory exists and is available before using the source file
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        compileJavaCode(sourceFile);
    }

    /**
     * Function responsible for creating a file (ReducerPrep) inside the ./temp folder holding the user code for the mapper function
     * Proceeding to run the code producing
     * @param method the method of reducing defined by the user. TODO: may need to change completely depending on front end
     * @param userImports imports defined by the user code
     * @throws IOException
     */
    public static void prepareReducerCode(String method, String userImports)
            throws IOException {
        String source =
                userImports
                        + "import java.io.*;\n" +
                        "public class ReducerUtil { " + method + " }";

        File root = new File("./temp");
        File sourceFile = new File(root, "reducer/ReducerPrep.java");
        //makes sure the parent directory exists and is available before using the source file
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        compileJavaCode(sourceFile);
    }

    /**
     * Function responsible for compiling a file containing java code
     * @param file The file to be compiled
     * @throws IllegalFormatCodePointException
     */
    public static void compileJavaCode(File file) throws IllegalFormatCodePointException{
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //Result code is 0 if everything runs correctly
        int resultCode = compiler.run(null, null, null, file.getPath());
        if (resultCode != 0) {
            throw new IllegalFormatCodePointException(1);
        }
    }

}
