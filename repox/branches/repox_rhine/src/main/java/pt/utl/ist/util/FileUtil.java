/*
 * ConfigFiles.java
 *
 * Created on 10 de Abril de 2002, 18:35
 */

package pt.utl.ist.util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author  Nuno Freire
 */
public class FileUtil {
    
    /** Creates a new instance of ConfigFiles */
    public FileUtil() {
    }

    /**
     * @param sourceFilename 
     * @param targetFilename
     * @param regExp - Regular expression to substitute
     * @param newText - text to place on the targetFile */    
    public static void substituteInFile(String sourceFilename,String targetFilename, String regExp, String newText){        
        File f1=new File(sourceFilename);
        File f2=new File(targetFilename);
        try {
            Pattern pattern = Pattern.compile(regExp);
            BufferedReader file1=new BufferedReader(new FileReader(f1));
            PrintWriter file2=new PrintWriter(new FileWriter(f2));
            try {
                String line=file1.readLine();
                while(line != null) {
                    Matcher m = pattern.matcher(line);
                    String s=m.replaceAll(newText);
                    file2.println(s);
                    line=file1.readLine();
                }
            } catch(EOFException ex) {
            }
            file2.close();
            file1.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    
    public static String readFileToString(File file) throws java.io.FileNotFoundException, java.io.IOException{
        StringBuffer ret=new StringBuffer();
//        char[] buf=new char[1024];
        char c=' ';
        FileReader reader=new FileReader(file);
        
        int r=0;
        while((r=reader.read()) != -1){
            ret.append((char)r);
        }
        reader.close();
        return ret.toString();
    }

    
    public static String readFileToString(File file, String encoding) throws java.io.FileNotFoundException, java.io.IOException{
    	byte[] bytes=readFileBytes(file);
        return new String(bytes,encoding);
    }
    
    
    // Returns the contents of the file in a byte array.
    public static byte[] readFileBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large"+file.getName());
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }    
    

    public static void writeToFile(File file, String data) throws java.io.FileNotFoundException, java.io.IOException{
        FileWriter f=new FileWriter(file);
        f.write(data);
        f.close();
    }

    public static void writeToFile(File file, String data, String encoding) throws java.io.FileNotFoundException, java.io.IOException{
    	FileOutputStream f=new FileOutputStream(file);    	
        f.write(data.getBytes(encoding));
        f.close();
    }
    
    
    public static void writeToFile(File file, byte[] data) throws java.io.FileNotFoundException, java.io.IOException{
    	FileOutputStream f=new FileOutputStream(file);    	
        f.write(data);
        f.close();
    }
    
    public static void writeToFile(File file, InputStream data) throws java.io.FileNotFoundException, java.io.IOException{
    	FileOutputStream fos=new FileOutputStream(file);    	
        transferData(data,fos);
        data.close();
        fos.close();
    }
    

    public static String getSystemCharset() {
    	return NUtil.getSystemCharset();
    }
    
    
    public static void copyFile(File source, File target) throws java.io.FileNotFoundException, java.io.IOException{
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(target);
        transferData(fis,fos);
        fis.close();
        fos.close();
    }    
        
    public static void copyFileToDir(File source, File targetDir) throws java.io.FileNotFoundException, java.io.IOException{
        copyFile(source,new File(targetDir,source.getName()));
    }
    
    public static void copyDirContents(File sourceDir, File targetDir) throws java.io.FileNotFoundException, java.io.IOException{
        File[] files=sourceDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File subTarget = new File(targetDir, file.getName());
                if (!subTarget.exists())
                    subTarget.mkdir();
                copyDirContents(file, subTarget);
            } else {
                copyFileToDir(file, targetDir);
            }
        }
    }
    

    public static boolean deleteDir(File sourceDir) throws java.io.FileNotFoundException, java.io.IOException{
        File[] files=sourceDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                boolean succ = file.delete();
                if (!succ)
                    return false;
            }
        }
        return sourceDir.delete();
    }    
    
    
    public static void transferData(InputStream in, OutputStream out) throws IOException{        
    	byte[] buf = new byte[4096];
	    int i = 0;
	    do {
	        i = in.read(buf);
	        if (i != -1) {
	         out.write(buf, 0, i);
	        }
	    } while (i != -1);    	
    }
}
