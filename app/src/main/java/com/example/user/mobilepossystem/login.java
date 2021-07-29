package com.example.user.mobilepossystem;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
   private Button btn_login;
    private  EditText editText_password1,editText_email1;
    private TextView btn_register,fpw;
    private  ProgressBar progressBar;
    private EditText phone_number;
    private DatabaseReference db;
    public String navigation_user_full_name,un;
    private boolean checked = false;
    private String session_name,shop_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseDatabase.getInstance().getReference("user");
        editText_email1=(EditText)findViewById(R.id.phone_number_id);
        editText_password1=(EditText)findViewById(R.id.editText_password);
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_register=(TextView) findViewById(R.id.btn_register);
        fpw=(TextView) findViewById(R.id.fpw);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        goto_login();
        goto_Register();

        fpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
    }

    private void forgotPassword() {
        LayoutInflater li = LayoutInflater.from(login.this);
        View promptsView = li.inflate(R.layout.prompt_forgot_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(login.this);
        alertDialogBuilder.setView(promptsView);
        final EditText pno =(EditText) promptsView.findViewById(R.id.pno);
        final EditText fn =(EditText) promptsView.findViewById(R.id.fn);
        final Button done= promptsView.findViewById(R.id.btn_ok);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(pno.getText())){
                    Toast.makeText(login.this,"Please enter phone number !",Toast.LENGTH_SHORT).show();
                }else if (!Patterns.PHONE.matcher(pno.getText()).matches() || !(pno.getText().length()>=10 && pno.getText().length()<=11)){
                    Toast.makeText(login.this,"Please insert valid phone number ! ",Toast.LENGTH_SHORT).show();
                    pno.setText(null);
                }else if(TextUtils.isEmpty(fn.getText())){
                    Toast.makeText(login.this,"Please enter full name !",Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference d = FirebaseDatabase.getInstance().getReference("user").child(pno.getText().toString());
                    d.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()){
                                User p = dataSnapshot.getValue(User.class);
                                if(p.getUser_full_name().equals(fn.getText().toString())){
                                    alertDialog.cancel();
                                    LayoutInflater li = LayoutInflater.from(login.this);
                                    View promptsView = li.inflate(R.layout.show_pw, null);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(login.this);
                                    alertDialogBuilder.setView(promptsView);
                                    final TextView password =(TextView) promptsView.findViewById(R.id.password);
                                    password.setText(p.getUser_password());
                                    alertDialogBuilder.setCancelable(true);
                                    final AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }else{
                                    Toast.makeText(login.this,"Invalid full name  !",Toast.LENGTH_SHORT).show();
                                    fn.setText(null);
                                }
                            }else{
                                Toast.makeText(login.this,"User does not exist !",Toast.LENGTH_SHORT).show();
                                pno.setText(null);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
    }
    public void goto_login(){
        btn_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(editText_email1.getText())){
                            Toast.makeText(login.this,"Please enter phone number !",Toast.LENGTH_LONG).show();
                        }else if (!Patterns.PHONE.matcher(editText_email1.getText()).matches() || !(editText_email1.getText().length()>=10 && editText_email1.getText().length()<=11)){
                            Toast.makeText(login.this,"Please enter valid phone number !",Toast.LENGTH_LONG).show();
                            editText_email1.setText(null);
                        }
                        else if(TextUtils.isEmpty(editText_password1.getText())) {
                            Toast.makeText(login.this, "Please enter password !", Toast.LENGTH_LONG).show();
                        }
                        else{
                            db.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot userSS : dataSnapshot.getChildren()){
                                        User  user = userSS.getValue(User.class);
                                        if(editText_email1.getText().toString().equals(user.getUser_phone_number())){
                                                if(editText_password1.getText().toString().equals(user.getUser_password())){
                                                    checked = true;
                                                    session_name = user.getUser_phone_number();
                                                    un = user.getUser_full_name();
                                                    shop_name = user.getUser_shop_name();
                                                    break;
                                                 }
                                            }
                                        }
                                        if(checked){
                                            progressBar.setVisibility(View.VISIBLE);
                                            editText_email1.setText(null);
                                            editText_password1.setText(null);
                                            checked=false;
                                            Toast.makeText(login.this,"Welcome back, "+un, Toast.LENGTH_LONG).show();
                                            Intent myIntent = new Intent(login.this, system_main_menu.class);
                                            myIntent.putExtra("Session ID",session_name);
                                            myIntent.putExtra("Shop",shop_name);
                                            myIntent.putExtra("UN",un);
                                            navigation_user_full_name = session_name;
                                            startActivity(myIntent);
                                        }else if ((!checked) && (!TextUtils.isEmpty(editText_email1.getText())) && (!TextUtils.isEmpty(editText_password1.getText()))) {
                                            Toast.makeText(login.this, "Invalid ID / Password.", Toast.LENGTH_LONG).show();
                                            editText_email1.setText(null);
                                            editText_password1.setText(null);
                                        }
                                    }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
        );
    }
    public void goto_Register(){
        btn_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText_email1.setText(null);
                        editText_password1.setText(null);
                        checked =false;
                        Intent myIntent = new Intent(login.this, register.class);
                        startActivity(myIntent);

                    }
                }
        );
    }
}
