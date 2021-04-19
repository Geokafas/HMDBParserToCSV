package com.HMDBUtil.Exporters;
import com.HMDBUtil.Preprocessing.DocumentPreProcessor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

//Creates the final .csv/.tsv file from the XML
public class ExportToFile {
    //How the file is going to be called
    public static String Uri;

    //class constructor
    public ExportToFile(String uri){
        Uri = uri;

    }

    //this method creates the lookup table based on an existing structure file of the XML
    public Map<Integer,String> writeToFileElementNodes(){
        StringBuilder title = new StringBuilder();
        DocumentPreProcessor preDoc = new DocumentPreProcessor();

        //Populate the elementsList with the structure of the XML file
        //based on the output of the DocumentPreProcessor
        Map<Integer,String> elementsList = preDoc.ConstructElementsMap();

        //append a tab to the end of every entry so it can be separated later on
        for (Map.Entry me : elementsList.entrySet()) {
            title.append(me.getValue()+"\t");
        }

        //write to the final output file the title of every XML element
        //this file will be appended later with the data of the parse
        //proses.
        try {
            FileOutputStream fileOut =  new FileOutputStream(Uri);
            fileOut.write(title.toString()
                    .getBytes(StandardCharsets.UTF_8));
            fileOut.write("\n".getBytes(StandardCharsets.UTF_8));
            fileOut.flush();
            fileOut.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return elementsList;
    }

    //a secondary method that prints the XML file as it is being parsed
    //helpful for debugging and with the understanding of the XML's structure.
    public void exp(char[] arr,String str){
        try {
            FileOutputStream fileOut =  new FileOutputStream("output.txt",true);

            //write field values:
            fileOut.write(str.getBytes(StandardCharsets.UTF_8));
            fileOut.write("\n".getBytes(StandardCharsets.UTF_8));
            for(int item=0; item<arr.length; item++){
                String getData = String.valueOf(arr[item]);
                fileOut.write(getData.getBytes(StandardCharsets.UTF_8));
            }
            fileOut.write("\n".getBytes(StandardCharsets.UTF_8));
            fileOut.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~".getBytes(StandardCharsets.UTF_8));
            fileOut.flush();
            fileOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //find the array with the largest amount of elements and return it's size
    //The input of this method is an array with the contents of an entry
    //as returned from the HMDHandler.
    public int findMax(ArrayList<ArrayList<String>> data){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i=0; i < data.size(); i++){
            list.add(data.get(i).size());
        }
        var max = Collections.max(list);
        return max;
    }

    //Responsible for the printing of the final csv/tsv file
    //Takes as input the array with the contents of an entry
    //as returned from the HMDHandler. Reads the array of arrays in
    //a horizontal fashion and places each value in the corresponding
    //XML title.
    public void makeFile(ArrayList<ArrayList<String>> data){
        try {
            FileOutputStream fileOut =  new FileOutputStream(Uri, true);

            //The SAXparser had some trouble with some data entries
            //So the correctOutput method addresses that.
            int maxLength = findMax(data);
            String metabolite_id = data.get(4).get(0);
            for( int i=1; i<maxLength; i++){
                data.get(4).add(i,metabolite_id);
            }

            //the first for loop iterates vertically from 0 to the max array size
            for(int k=0; k<maxLength; k++) {
                //inside the buffer strings are placed with a tab in between each entry
                StringBuilder buffer= new StringBuilder();
                //the second loop iterates horizontally through the full length of the array of arrays(data array).
                for (int i = 0; i < data.size(); i++) {
                    //if that cell has value then write it to the buffer
                    if(!data.get(i).isEmpty()){
                        data.get(i).removeAll(Arrays.asList(null, ""," "));
                        //The way the characters method reads from the XML means that some times,
                        //a data that belong to the same entry are read in many passes. So tabs are
                        //placed in the end. In the final file we want a tab character only in the end of an entry
                        //so in a .tsv scenario data from an entry don't get mixed with data from another.
                        //In a .tsv data entries are separated on tabs, in a .csv on commas.
                        buffer.append(data.get(i).get(0).trim().replace("\t"," ")+"\t");
                        data.get(i).remove(0);
                    }else{//if not
                        //the arrays inside of arrays (the arrays that contain the actual data of an entry)
                        //do NOT have the same length. Because an element in an XML entry may contain an array and
                        //its neighbouring a simple integer or a string... So in order for the final output to
                        //be ordered correctly i must compensate for this with a tab.
                        buffer.append("\t");
                    }
                }

                fileOut.write(buffer.toString().getBytes(StandardCharsets.UTF_8));
                fileOut.write("\n".getBytes(StandardCharsets.UTF_8));
                fileOut.flush();
                buffer.setLength(0);
            }
            fileOut.close();
            System.out.println("File Written Successfully");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
