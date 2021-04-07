package com.HMDBUtil.core;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Main extends DefaultHandler {
    //the path of the XML file is HARDCODED
    private static final String HMDBFile = "hmdb_metabolites.xml";

    public static void main(String[] args) {
        //initialize a handler object
        HMDBHandler hHandler = new HMDBHandler();
        System.out.println("Init Parser");

        try {
            //init a parse factory
            SAXParserFactory sfactor = SAXParserFactory.newInstance();
            //init the parser
            SAXParser saxParser = sfactor.newSAXParser();
            //Invoke main Parse procedure
            saxParser.parse(HMDBFile, hHandler);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
