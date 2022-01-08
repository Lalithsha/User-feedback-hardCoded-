package com.lalithsharma.userfeedback;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    EditText  etSubject,etMessage;
    TextView etTo;
    Button button;
    String sEmail,sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.editTextTo);
      //  etSubject = findViewById(R.id.editTextSubject);
        etMessage = findViewById(R.id.editTextMessage);
        button = findViewById(R.id.button);

        // Senders email credentials
        sEmail = "lalithsharma.mech@gmail.com";
        sPassword = "google81";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize properties
                Properties properties = new Properties();
                properties.put("mail.smtp.auth","true");
                properties.put("mail.smtp.starttls.enable","true");
                properties.put("mail.smtp.host","smtp.gmail.com");
                properties.put("mail.smtp.port","587");

                // Initialize session
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sEmail,sPassword);
                    }
                }) ;


                try {
                    // Initialize email content
                    Message message = new MimeMessage(session);
                    // sender email
                    message.setFrom(new InternetAddress(sEmail));
                    // Recipient email
//                    message.setRecipients(Message.RecipientType.TO, etTo.setText("lalithsharma989@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(etTo.getText().toString().trim()));

                    // Email subject
                   // message.setSubject(etSubject.getText().toString().trim());

                    // Email message
                    message.setText(etMessage.getText().toString().trim());

                    // Send Email
                    new SendMail().execute(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class SendMail extends AsyncTask<Message,String,String> {

        //Initialize progress dialog
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Create and show progress dialog
            progressDialog = ProgressDialog.show(MainActivity.this,"Please wait",
                    "Sending Mail...",true,false);
        }


        @Override
        protected  String doInBackground(Message...messages){

          try {
              // When success
              Transport.send(messages[0]);
              return "Success";
          } catch (MessagingException e){
              // When error
              e.printStackTrace();
              return "Error";
          }
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            // Dismiss progress dialog
            progressDialog.dismiss();
            if(s.equals("Success")) {
                //When success

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#509324'>Success</font>"));
                builder.setMessage("Mail send successfully");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Clear all edit text
                      //  etTo.setText("");

                      //  etSubject.setText("");

                        etMessage.setText("");
                    }
                });
                // Show alert dialog
                builder.show();
            }else{
                // When error
                Toast.makeText(getApplicationContext(),
                        "Something went wrong ?",Toast.LENGTH_SHORT).show();
            }
        }
    }
}