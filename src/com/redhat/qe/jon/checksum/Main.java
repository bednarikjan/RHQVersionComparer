package com.redhat.qe.jon.checksum;

/**
 * // TODO: Document this
 *
 * @author ahovsepy
 */
import java.io.BufferedReader;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {


   /**
    * Java program to generate MD5 checksum for files in Java. This Java example
    * uses core Java security package to generate MD5
    * checksum for a File.
    *
    *
    */
   private static final Logger logger = Logger.getLogger(Main.class.getName());
   private static HashMap<String, String> map  = new HashMap<String, String>();
   private static final String fileName = "filesHash.txt";

   public static void main(String args[]) throws IOException {
      String baseDir = args[0];
      String ifCompare = null;
      if(args.length >1){
         ifCompare = args[1];
      }
      getFiles(baseDir);

      if(ifCompare != null && ifCompare.equals("compare")){
         HashMap<String, String> hashMap = readMapFromFile();
         compareMaps(hashMap);
      }
      else {
         writeHashToFile();
      }

   }
   public static void compareMaps(HashMap<String, String> oldMap){
      for(Map.Entry<String, String> m: map.entrySet())   {
         if(oldMap.get(m.getKey()) != null){
            if(!oldMap.get(m.getKey()).equals(m.getValue())){
               logger.info("for "+m.getKey()+" hash was "+oldMap.get(m.getKey())+" new hash is "+m.getValue());

            }
            oldMap.remove(m.getKey());
         }
         else {
            logger.info("New file added "+m.getKey());
         }

      }
      if(oldMap.size() > 0){
          for (Map.Entry<String, String> m: oldMap.entrySet())
          {
             logger.info(m.getKey()+" file has been removed");
          }

      }

   }
   public static HashMap<String, String> readMapFromFile() throws IOException {
      HashMap<String, String> hmap = new HashMap<String, String>();
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      try{
      String line = null;
      while ((line=br.readLine())!=null)
      {
         String [] str = line.split(",");
         hmap.put(str[0],str[1]);
      }
      }
      finally{
         br.close();
         fr.close();
      }
      return hmap;
   }

   public static void  writeHashToFile() throws IOException {
      FileWriter fw = new FileWriter(fileName);
      try{
         for (Map.Entry<String, String> m: map.entrySet())   {
            fw.write(m.getKey() + "," + m.getValue()+"\n");
         }
      }
      finally{
         fw.close();
      }

   }
   public static void getFiles(String baseDir){

      File file =  new File(baseDir);
      File [] files = file.listFiles();

      for (File f : files){
         if (f.isDirectory()){
            getFiles(f.getAbsolutePath()) ;
         }
         else {
            map.put(f.getAbsolutePath(), checkSum(f.getAbsolutePath()));

         }
      }

   }
   /*
    * Calculate checksum of a File using MD5 algorithm
    */
   public static String checkSum(String path){
      String checksum = null;
      try {
         FileInputStream fis = new FileInputStream(path);
         MessageDigest md = MessageDigest.getInstance("MD5");

         //Using MessageDigest update() method to provide input
         byte[] buffer = new byte[8192];
         int numOfBytesRead;
         while( (numOfBytesRead = fis.read(buffer)) > 0){
            md.update(buffer, 0, numOfBytesRead);
         }
         byte[] hash = md.digest();
         checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
      } catch (IOException ex) {
         logger.log(Level.SEVERE, null, ex);
      } catch (NoSuchAlgorithmException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

      return checksum;
   }


}

