package com.carrental.ShivaSD.bottomNav.home.mailer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class sendMail extends AsyncTask<Void,Void,String>{

   private static final String EMAIL ="1shivatourandtravel@gmail.com";
   private static final String PASSWORD ="1shivatoursandtraveL";

   @SuppressLint("StaticFieldLeak")
   private final Context context;
   private final String email;
   private final String subject;
   private final String message;
   private ProgressDialog progressDialog;

   public sendMail(String subject, String message, String email, Context context){
      this.context = context;
      this.email = email;
      this.subject = subject;
      this.message = message;
   }

   @Override
   protected void onPreExecute() {
      super.onPreExecute();
      progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
   }

   @Override
   protected void onPostExecute(String exeMsg) {
      progressDialog.dismiss();
      Toast.makeText(context,exeMsg,Toast.LENGTH_LONG).show();
   }

   @Override
   protected String doInBackground(Void ... param) {
      Properties props = new Properties();
      props.put("mail.smtp.host", "smtp.gmail.com");
      props.put("mail.smtp.socketFactory.port", "465");
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.port", "465");
      Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(EMAIL, PASSWORD);
         }
      });
      try {
         MimeMessage mm = new MimeMessage(session);
         mm.setFrom(new InternetAddress(EMAIL));
         mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
         mm.setSubject(subject);
         mm.setText(message);
         Transport.send(mm);
      }
      catch (MessagingException e) {
         e.printStackTrace();
      }
      return "Booking Successful";
   }
}