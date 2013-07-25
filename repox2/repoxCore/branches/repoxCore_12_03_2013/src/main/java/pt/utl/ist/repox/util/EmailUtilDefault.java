package pt.utl.ist.repox.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class EmailUtilDefault implements EmailUtil{
//	possible hosts: "mail.clix.pt"; "mail.ist.utl.pt"; "smtp.ist.utl.pt"; "mail.inesc-id.pt"; "smtp.inesc-id.pt"; "inesc-id.inesc-id.pt";
//	/home/dreis/temp/lixo.csv
//  repox@ist.utl.pt

    public void sendEmail(String fromEmail, String[] recipientsEmail,
                          String subject, String message, File[] attachments, HashMap<String, Object> map) throws MessagingException, FileNotFoundException {
        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer());
        props.put("mail.smtp.socketFactory.port", ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpPort());

        // create some properties and get the default Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail(), ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getMailPassword());
                    }
                });
        session.setDebug(false);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(fromEmail);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[1];
        for (int i = 0; i < recipientsEmail.length; i++)
        {
            addressTo[i] = new InternetAddress(recipientsEmail[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);

        if(attachments == null || attachments.length == 0) {
            // Create the message part
            msg.setText(message);
        }
        else {
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(message);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Fill attachments
            for (File file : attachments) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(file.getName());
                multipart.addBodyPart(messageBodyPart);
            }

            // Put parts in message
            msg.setContent(multipart);
        }

        Transport.send(msg);

        // delete zip files on attachments
        if(attachments != null){
            for(File file : attachments){
                if(file.getName().contains(".zip"))
                    file.delete();
            }
        }
    }

    public File createZipFile(File logFile){
        // These are the files to include in the ZIP file
        String[] files = new String[]{logFile.getAbsolutePath()};
        String[] filenames = new String[]{logFile.getName()};

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        File zippedFile = new File(logFile.getParentFile().getAbsolutePath()+ File.separator + logFile.getName()+".zip");
        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zippedFile));

            // Compress the files
            for (int i=0; i<filenames.length; i++) {
                FileInputStream in = new FileInputStream(files[i]);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filenames[i]));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
        }
        return zippedFile;
    }


    public static void main(String[] args) throws Exception {
//        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
//        String smtpServer = "smtp.gmail.com";
//        String fromEmail = "repox@noreply.eu";
//        String[] recipientsEmail = new String[]{"repoxist@gmail.com"};
//
//        //File[] attachments = new File[] {new File("C:\\Users\\Gilberto Pedrosa\\Desktop\\ViewDataProvider.txt"), new File("/home/dreis/temp/lixo.csv")};
//        File[] attachments = new File[] { new File("C:\\Users\\Gilberto Pedrosa\\Desktop\\ViewDataProvider.txt") };
//        EmailUtilDefault emailUtilDefault = new EmailUtilDefault();
//        emailUtilDefault.sendEmail(fromEmail, recipientsEmail, "REPOX email", "Test message", attachments, null);
        File newFile = new File("D:\\Projectos\\TESTS\\threads.xml");
        EmailUtilDefault emailUtilDefault = new EmailUtilDefault();
        emailUtilDefault.createZipFile(newFile);

        System.exit(0);
    }
}
