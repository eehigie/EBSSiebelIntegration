/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class FunctionLogging {
    private String path;
    private boolean appendToFile = false;
	
    public FunctionLogging(String file_path, boolean append_value)
    {
	path = file_path;
	appendToFile = append_value;
    }
    public FunctionLogging(){
        
    }
    public void writeToFile(String textLine)
    {
	Date date = new Date();
	SimpleDateFormat format = new SimpleDateFormat();
	format = new SimpleDateFormat("dd-MM-yyyy|HH:mm:ss:SSSSSS|");
	String timeStamp = format.format(date);
	SimpleDateFormat app = new SimpleDateFormat();
	app = new SimpleDateFormat("dd-MM-yyyy");
	String dateApp = app.format(date);
	String temp = UUID.randomUUID().toString();
	String unique = temp.substring(28);

	try{
            FileInputStream propFile = new FileInputStream("config.ini");
            Properties config = new Properties(System.getProperties());
            config.load(propFile);
            path = config.getProperty("path");
            FileWriter write = new FileWriter(path+dateApp+".log", appendToFile);
            PrintWriter printlines = new PrintWriter(write);
            printlines.printf("%s" + "%n"+ timeStamp+unique+"|", textLine);
            printlines.close();		
	}catch(IOException e){
            e.printStackTrace();
	}
    }
    
    public void writeToLog(File logFile, String strLogFile, String txt){
        try{
            //String data = " This content will append to the end of the file";
    		
            //File file =new File("javaio-appendfile.txt");
            String temp = UUID.randomUUID().toString();
            String unique = temp.substring(28);
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat();
            format = new SimpleDateFormat("dd-MM-yyyy|HH:mm:ss:SSSSSS|");
            String timeStamp = format.format(date);
            //if file doesnt exists, then create it
            if(!logFile.exists()){
    		logFile.createNewFile();
            }
    		
            //true = append file
           /* FileWriter fileWritter = new FileWriter(logFile.getName(),true);
            try (BufferedWriter bufferWritter = new BufferedWriter(fileWritter)) {
                bufferWritter.write(txt);
                bufferWritter.close();
             }*/
            FileWriter write = new FileWriter(strLogFile,true);
            PrintWriter printlines = new PrintWriter(write);
            //printlines.printf("%s" + "%n"+ timeStamp+unique+"|", txt);
            printlines.printf("%s" + "%n"+ timeStamp+"|", txt);
            printlines.close();	
                
            
           
	        
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
