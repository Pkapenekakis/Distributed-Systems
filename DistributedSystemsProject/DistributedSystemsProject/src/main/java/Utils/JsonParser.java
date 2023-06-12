package Utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;


public class JsonParser {
    int numOfReducers;
    int numOfMappers;
    String imports;
    String mapperCode;

    String reducerCode;

    /**
     * Function responsible for reading the JSON file with the user data
     * @param filePath the path to the JSON file
     */
    public void readJson(String filePath){
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject)obj;
            setNumOfMappers((int)jsonObject.get("numOfMappers"));
            setNumOfReducers((int)jsonObject.get("numOfReducers"));
            setImports((String)jsonObject.get("Imports") );
            setMapperCode((String)jsonObject.get("Mapper") );
            setReducerCode((String)jsonObject.get("Reducer") );
        }catch(Exception e){
            System.out.println("Error parsing JSON file");
        }
    }



    public String getMapperCode() {
        return mapperCode;
    }

    public void setMapperCode(String mapperCode) {
        this.mapperCode = mapperCode;
    }

    public String getReducerCode() {
        return reducerCode;
    }

    public void setReducerCode(String reducerCode) {
        this.reducerCode = reducerCode;
    }

    public String getImports() {
        return imports;
    }

    public void setImports(String imports) {
        this.imports = imports;
    }

    public int getNumOfReducers() {
        return numOfReducers;
    }

    public void setNumOfReducers(int numOfReducers) {
        this.numOfReducers = numOfReducers;
    }

    public int getNumOfMappers() {
        return numOfMappers;
    }

    public void setNumOfMappers(int numOfMappers) {
        this.numOfMappers = numOfMappers;
    }
}
