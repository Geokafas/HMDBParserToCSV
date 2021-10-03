package com.HMDBUtil.core;
import com.HMDBUtil.Exporters.ExportToFileSingleLines;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.util.*;

public class HMDBHandler extends DefaultHandler {

    //private final ExportToFile exporter = new ExportToFile("hmdbMetabolites.csv");
    private final ExportToFileSingleLines singleLineExporter =  new ExportToFileSingleLines("hmdbMetabolitesSingleLinesCustomized.csv");
    //That is the structure that holds the data extracted from the parse operation
    //of an XML file. It hold a key and an arraylist that contains the parsed data.
    //I used a list because some of the data Nodes contained tables instead of simple
    //character values.
    //This hashMap is populated and emptied(also printed) every time the parser finds a new entry.
    private Map<Integer,ArrayList<String>> nodeValuesHashMap =  new HashMap<Integer,ArrayList<String>>();

    //The lookup table is calculated at the very beginning of this program.
    //In this particular implementation is hard coded. Meaning that a i created a txt file
    //with the structure of the XML file beforehand so a reader populates this lookup array
    //with strings found in that structure file.
    //It is used as a lookup table from the findParsed method in order to assign an id to each node
    //of the XML file. This id is then used to add parsed data to the correct position of the hashmap.
    private Map<Integer,String> lookup = new HashMap<>();

    //I am keeping each element i am currently processing inside a heap so i know,
    // the character i'm reading to which element they belong.
    // The heap is a mechanism that tells the parser the relative position, in terms of element tags,
    //of the document. Because the characters method is agnostic of position, and does not know if the end of
    //a line signals the end of an element or not. This way i am ensuring that all the data belonging to an
    //element(Node) is added to the correct position in the nodeValuesHashMap.
    private ArrayList<String> heap = new ArrayList<>();

    //this variable is the key of every element in the nodeValuesHashMap
    //It is calculated based on the position of an element's(node) tag in the lookup array
    private int parsedId;

    //part of the SAX library. Called before the the parser reads the first line
    //of the document
    public void startDocument(){
        //populate the lookup table
        lookup = singleLineExporter.writeToFileElementNodes();
        //populate nodesValueHashMap
        for(int i=0; i<lookup.size(); i++){
            nodeValuesHashMap.put(i,new ArrayList<>());
        }
    }

    // Called when character data is encountered by the SAXParser
    // That is after the startElement method has found an opening element tag
    // and before the endElement find it's closing
    public void characters(char[] buffer, int start, int length) {
        //some times the parser reads empty characters
        //to compensate for that a trim the string and pass it
        //only if it's trimmed length is above zero
        if (new String(buffer,start,length).trim().length() > 0){
            //the parsedId has the value assigned to it by the findParsedId method
            nodeValuesHashMap.get(parsedId).add(new String(buffer,start,length).replaceAll("\n"," "));
        }
    }

    //Called every time the parser recognizes the starting tag of an element
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            //when the hmdb tag is found, recognize the start of the XML document.
            //this tag also signal the end of the whole proses.
            if (qName.equalsIgnoreCase("hmdb")){
                heap.add(qName);
                parsedId = 0;
            }else {
                //find an id for the element being parsed based on it's position in the lookup table.
                parsedId = findParseId(qName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Called every time the parser recognizes the ending tag of an element
    public void endElement(String uri, String localName, String qName){
        try {
            //recognises the repetition of the tags in the document.
            //As "metabolite" is the parent element of each entry in the XML
            //and child of the hmdb tag.
            if(qName.equalsIgnoreCase("metabolite")){
                //print that XML entry to a file
                //i am copying it to an arraylist because there is unexpected behaviour
                //if printing directly from the map.
                ArrayList<ArrayList<String>> data = new ArrayList();
                for(int i=0; i<nodeValuesHashMap.size(); i++){
                    data.add(nodeValuesHashMap.get(i));
                }
                //call the exporter
                singleLineExporter.makeFile(data);
                //prepare for reading the next entry by restarting
                // the global maps and id variables
                restartGlobals();

            }else{
                //when a closing tag is found delete that entry from the heap
                deleteFromHeap(qName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //reinitialize the nodeValuesHashMap and the heap each time
    //before each document repetition (new entry)
    private void restartGlobals(){
        for(int i=0; i< lookup.size(); i++){
            //restart values hashmap
            nodeValuesHashMap.put(i,new ArrayList<>());
        }

        //restart heap
        heap = new ArrayList<String>();
        //the heap has always hmdb as the head
        //because hmdb states the beginning and end of the whole
        //XML file
        heap.add("hmdb");
        //reset the current id indicator to zero
        parsedId = 0;
    }

    //this method recognizes when a different element tag is parsed
    //and assigns it an unique id
    private int findParseId(String qName){
        int id = -1;
        //add the element tag name in to the heap
        heap.add(qName);

        //build a string so it can be compared with the lookup array
        StringBuilder temp = new StringBuilder();
        for(String str: heap){
            temp.append(str).append("/");
        }
        StringBuilder sub = temp.deleteCharAt(temp.length()-1);
        //System.out.println(sub);

        //compare this string with those in the lookup array
        //and if it maches with someone then its possition in the
        //lookup array becomes this element's id
        for(int i=0; i<lookup.size();i++){
            if(lookup.get(i).equals(sub.toString())){
                id=i;
                break;
            }
        }
        return id;
    }

    //this method removes an entry from the heap
    //when it's matching end tag is parsed by the endElement method
    private void deleteFromHeap(String qName){
        //some elements have ONLY end tags because they are not populated with
        //values. So i have to account for that by checking whether or not the last
        //item in the heap is equal to the qname that called deleteFromHeap
        //System.out.println("heap: "+ heap.get(heap.size()-1) + "qName: "+qName);
        if(qName.equalsIgnoreCase(heap.get(heap.size()-1))){
            heap.remove(heap.size()-1);
            parsedId -= 1;
        }else{
            System.out.println("element: "+qName+" has no value");
        }
    }

    //part of the SAX library. Called after the the parser reads the last line
    //of the document
    public void endDocument(){
        System.out.println("Parsing complete! ");
    }
}
