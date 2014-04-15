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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    /**
     * Java program to generate MD5 checksum for files in Java. This Java
     * example uses core Java security package to generate MD5 checksum for a
     * File.
     *
     *
     */
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static HashMap<String, String> jonFiles = new HashMap<String, String>();
    private static HashMap<String, String> updateFiles = new HashMap<String, String>();
    private static final String fileName = "filesHash.txt";    

    private static int chanRemCount = 0;    

    public static void main(String args[]) throws IOException {
        boolean findBackUp = false;
        String baseDirJon = new String();
        String baseDirUpdate = new String();        
        
        if (args.length == 0) {
            logger.info("Param: Path to RHQ directory missing");
            return;
        }

        baseDirJon = args[0];

        if (args.length > 1) {
            findBackUp = true;
            baseDirUpdate = args[1];
        }

        getFiles(baseDirJon, jonFiles, (new File(baseDirJon)).getAbsolutePath().length() + 1);

        if (findBackUp) {
            HashMap<String, String> oldFiles = readMapFromFile();
            getFiles(baseDirUpdate, updateFiles, (new File(baseDirUpdate)).getAbsolutePath().length() + 1);
            
            findBackupFiles(oldFiles);

            System.out.println("Changed/removed files: " + chanRemCount);            
        } else {
            writeHashToFile();
        }

    }

    public static void findBackupFiles(HashMap<String, String> oldMap) {        
        for (Map.Entry<String, String> old : oldMap.entrySet()) {
            // file was modified or removed                        
            if((jonFiles.containsKey(old.getKey()) && !old.getValue().equals(jonFiles.get(old.getKey()))) ||
               !jonFiles.containsKey(old.getKey())) {              
                
                // check if the file is backed up
                if(!updateFiles.containsKey(old.getKey()) || 
                   !updateFiles.get(old.getKey()).equals(old.getValue())) {
                    logger.info("File NOT backed up: " + old.getKey());
                }
                
                chanRemCount++;
            }
        }

    }

    public static HashMap<String, String> readMapFromFile() throws IOException {
        HashMap<String, String> hmap = new HashMap<String, String>();
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] str = line.split(",");
                hmap.put(str[0], str[1]);
            }
        } finally {
            br.close();
            fr.close();
        }
        return hmap;
    }

    public static void writeHashToFile() throws IOException {
        FileWriter fw = new FileWriter(fileName);
        try {
            for (Map.Entry<String, String> m : jonFiles.entrySet()) {
                fw.write(m.getKey() + "," + m.getValue() + "\n");
            }
        } finally {
            fw.close();
        }

    }   
    
    public static void getFiles(String baseDir, HashMap<String, String> map, int pathToRemove) {

        File file = new File(baseDir);
        File[] files = file.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                getFiles(f.getAbsolutePath(), map, pathToRemove);
            } else {
                map.put(f.getAbsolutePath().substring(pathToRemove), checkSum(f.getAbsolutePath()));

            }
        }

    }
    /*
     * Calculate checksum of a File using MD5 algorithm
     */

    public static String checkSum(String path) {
        String checksum = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("MD5");

            //Using MessageDigest update() method to provide input
            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while ((numOfBytesRead = fis.read(buffer)) > 0) {
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
