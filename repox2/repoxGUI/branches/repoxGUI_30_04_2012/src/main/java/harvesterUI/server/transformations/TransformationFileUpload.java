package harvesterUI.server.transformations;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import pt.utl.ist.repox.util.ConfigSingleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 27-05-2011
 * Time: 11:43
 */
public class TransformationFileUpload extends HttpServlet {

    protected static File tempFile;
    protected String transformationID = "dummy";

    public void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        try{
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            List<FileItem> fileItems = upload.parseRequest(request);

            for(FileItem item: fileItems) {
                String name = item.getFieldName();
                InputStream stream = item.getInputStream();

                if(name.equals("transformationSubmitID")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int len;
                    byte[] buffer = new byte[8192];
                    while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    System.out.println("ID = " + out.toString());
                    transformationID = out.toString();
                    continue;
                }

                File xsltDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXsltDir();
                if(!xsltDir.exists())
                    xsltDir.mkdirs();

                tempFile = new File(xsltDir, item.getName().toLowerCase());

                // Process the input stream
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[8192];
                while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }

                int maxFileSize = 10485760; //10 megs max
                if (out.size() > maxFileSize) {
                    System.out.println("File is > than " + maxFileSize);
                    return;
                }

                FileWriter fstream = new FileWriter(tempFile);
                BufferedWriter outFile = new BufferedWriter(fstream);
                outFile.write(out.toString());
                outFile.close();

                System.out.println("Done transformation file upload");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static File getZ39TempFile() {
        return tempFile;
    }

    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
}

