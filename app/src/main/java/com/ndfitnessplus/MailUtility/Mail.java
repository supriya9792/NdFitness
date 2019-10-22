package com.ndfitnessplus.MailUtility;

import android.graphics.Bitmap;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Stream;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail extends javax.mail.Authenticator {
private String _user;
private String _pass;

private String[] _to;
private String _from;

private String _port;
private String _sport;

private String _host;

private String _subject;
private String _body;
private String fileName;
private String filePath;

private boolean _auth;

private boolean _debuggable;

private Multipart _multipart;
private File imagefile;

public Mail() {
        _host = "smtp.gmail.com"; // default smtp server
        _port = "587"; // default smtp port
        _sport = "587"; // default socketfactory port

       // _user = "tulsababar01@gmail.com"; // username
        //_pass = "Tulsa@2019"; // password
        _from = "tulsababar01@gmail.com"; // email sent from
        _subject = ""; // email subject
        _body = ""; // email body

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

// There is something wrong with MailCap, javamail can not find a handler for the multipart
// /mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        }

public Mail(String user, String pass) {
        this();

        _user = user;
        _pass = pass;
        }

public boolean send() throws Exception {
        Properties props = _setProperties();

        if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") &&
        !_subject.equals("") && !_body.equals("")) {



        SMTPAuthenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, null);
        session.setDebug(true);

                MimeMessage msg = new MimeMessage(session);
                try {
                        msg.setSubject(_subject);
                        msg.setSentDate(new Date());
                        msg.setFrom(new InternetAddress(_from));
                        msg.setText(_body);

                       // msg.addRecipient(Message.RecipientType.TO, new InternetAddress(_to));
                        InternetAddress[] addressTo = new InternetAddress[_to.length];
                        for (int i = 0; i < _to.length; i++) {
                                addressTo[i] = new InternetAddress(_to[i]);
                        }
                        msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
                       // String message = "<div style=\"color:red;\">BRIDGEYE</div>";
                        //msg.setContent(_body, "text/html; charset=utf-8");
                        msg.setSentDate(new Date());
                        // setup message body
                        //Attachment of image file QR code
                        if(imagefile !=null) {
                                BodyPart messageBodyPart = new MimeBodyPart();
                                messageBodyPart.setText(_body);
//                                messageBodyPart.attachFile(imagefile);
                                _multipart.addBodyPart(messageBodyPart);
                                messageBodyPart = new MimeBodyPart();
                                //String filename = "/home/manisha/file.txt";
                                DataSource source = new FileDataSource(fileName);
                                messageBodyPart.setDataHandler(new DataHandler(source));
//                                long n  = System.currentTimeMillis() / 1000L;
                                messageBodyPart.setFileName(filePath);
                                _multipart.addBodyPart(messageBodyPart);
                                //Put parts in message
                                msg.setContent(_multipart);

                        }else{
                                msg.setContent(_body, "text/html; charset=utf-8");
                        }
                        Transport transport = session.getTransport("smtp");
                        //transport.connect(_host,Integer.valueOf(_port),_user,_pass);
                        transport.connect(_host,_user,_pass);
                        //transport.connect(_user,_pass);
                        transport.sendMessage(msg, msg.getAllRecipients());
                        transport.close();

                } catch (AddressException e) {
                        e.printStackTrace();
                        return false;
                } catch (MessagingException e) {
                        e.printStackTrace();
                        return false;
                }
//        javax.mail.Message msg = new MimeMessage(Session
//        .getDefaultInstance(props, auth));
//        msg.setFrom(new InternetAddress(_from));
//
//        InternetAddress[] addressTo = new InternetAddress[_to.length];
//        for (int i = 0; i < _to.length; i++) {
//        addressTo[i] = new InternetAddress(_to[i]);
//        }
//        msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
//
//        msg.setSubject(_subject);


// setup message body
//        BodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setText(_body);
//        _multipart.addBodyPart(messageBodyPart);
//
//// Put parts in message
//        msg.setContent(_multipart);
//        Transport.send(msg);

// send email
//        String protocol = "smtp";
//        props.put("mail.smtp.auth", "true");
//      //  props.put("mail." + protocol + ".auth", "true");
//        Transport t = session.getTransport(protocol);
//        try {
//        t.connect("smtp.gmail.com","supriyab9792@gmail.com","Friends@1990");
//        t.sendMessage(msg, msg.getAllRecipients());
//        } finally {
//        t.close();
//        }

        return true;
        } else {
        return false;
        }
        }

@Override
public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
        }

private Properties _setProperties() {
        //Properties props = new Properties();
        Properties props = (Properties)System.getProperties().clone();
       // props.put("mail.smtp.host", "whatever");
        props.put("mail.smtp.host", _host);



       // props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.user", _user);
       // props.put("mail.smtp.host", _host);
        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.starttls.enable","true");
        //props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
      //  props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.debug", "true");
       // props.put("mail.smtp.auth", "true");
       // props.setProperty("mail.host", _host);
        props.put("mail.smtp.auth", "true");
        //props.put("mail.smtp.port", "465");
//        props.put("mail.smtp.socketFactory.port", "587");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.socketFactory.fallback", "false");
        //props.setProperty("mail.smtp.quitwait", "false");

        return props;
        }

// the getters and setters
public String getBody() {
        return _body;
        }

public void setBody(String _body) {
        this._body = _body;
        }
public void setTo(String[] to) {
        this._to = to;
        }
public void setFrom(String from) {
        this._from = from;
        }
public void setSubject(String subject) {
        this._subject = subject;
        }
  public  void setAttachment(File file){
             this.imagefile=file;
  }
  public  void setAttachmentName(String file){
             this.fileName=file;
  }
  public  void setAttachmentNamePath(String file){
             this.filePath=file;
  }

}
