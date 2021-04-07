package com.HMDBUtil.Preprocessing;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//reads the titleList.csv and extracts the XML nodes in a HasMap
public class DocumentPreProcessor {

    public Map<Integer, String> ConstructElementsMap() {
        int counterId=0;
        Map<Integer, String> elementsMap = new HashMap<>();

        try {
            Scanner myReader = new Scanner(new File("src/titleList.csv"));
            while (myReader.hasNextLine()) {
                //read line by line
                String data = myReader.nextLine();
                //remove ","
                String normalized = data.split(",")[0];
                //split to "/" and store all parts to an array
                //ArrayList<String> temp = new ArrayList<String>(Arrays.asList(normalized.split("/")));
                //reverse the orientation so the last title of the pathway is first
                //Collections.reverse(temp);
                //Construct to HashMap with all the XML document titles
                //elementsMap.put(counterId,temp.get(0));
                elementsMap.put(counterId,normalized);
                counterId++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println("Node list ready");
        return elementsMap;
    }
}
