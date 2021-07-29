package com.example.user.mobilepossystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class register extends AppCompatActivity {
   private EditText editText_full_name,editText_phone_number,editText_password,editText_repassword,editText_shop_name;
   private Button  btn_register;
 private boolean checked,show_message;
   private  String full_name,phone_number,pw,pw2,sn;
   DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseDatabase.getInstance().getReference("user");
        editText_full_name = (EditText)findViewById(R.id.editText_full_name);
        editText_phone_number = (EditText)findViewById(R.id.phone_number_id);
        editText_password = (EditText)findViewById(R.id.editText_password);
        editText_repassword= (EditText)findViewById(R.id.editText_repassword);
        editText_shop_name= (EditText)findViewById(R.id.editText_shop_name);
        btn_register = (Button)findViewById(R.id.btn_register);
        register();
    }
    public void register(){
        btn_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       full_name = editText_full_name.getText().toString();
                       phone_number = editText_phone_number.getText().toString();
                       pw = editText_password.getText().toString();
                       pw2 = editText_repassword.getText().toString();
                       sn=editText_shop_name.getText().toString();
                        checked=false;
                        show_message = true;
                        if(TextUtils.isEmpty(editText_full_name.getText())){
                            Toast.makeText(register.this,"Please enter your name !",Toast.LENGTH_LONG).show();
                        }
                        else if(TextUtils.isEmpty(editText_phone_number.getText())){
                            Toast.makeText(register.this,"Please enter your phone number !",Toast.LENGTH_LONG).show();
                        }else if (!Patterns.PHONE.matcher(editText_phone_number.getText()).matches() || !(editText_phone_number.getText().length()>=10 && editText_phone_number.getText().length()<=11)){
                            Toast.makeText(register.this,"Please enter valid phone number !",Toast.LENGTH_LONG).show();
                            editText_phone_number.setText(null);
                        }else if(TextUtils.isEmpty(editText_password.getText())){
                            Toast.makeText(register.this,"Please enter your password !",Toast.LENGTH_LONG).show();
                        }else if(TextUtils.isEmpty(editText_repassword.getText())){
                            Toast.makeText(register.this,"Please enter again your password !",Toast.LENGTH_LONG).show();;
                        }else if(!pw.equals(pw2)){
                            Toast.makeText(register.this,"Your password is not matched !",Toast.LENGTH_LONG).show();;
                        }else if(TextUtils.isEmpty(editText_shop_name.getText())){
                            Toast.makeText(register.this,"Please enter your shop name !",Toast.LENGTH_LONG).show();;
                        }else{
                            db.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot userSS : dataSnapshot.getChildren()){
                                        User  user = userSS.getValue(User.class);
                                        if(editText_phone_number.getText().toString().equals(user.getUser_phone_number())){
                                            checked = true;
                                            break;
                                        }
                                    }
                                    if(checked && show_message ){
                                        Toast.makeText(register.this,"This phone number had been registered !",Toast.LENGTH_LONG).show();
                                        editText_phone_number.setText(null);
                                        show_message =false;
                                    }else if(!checked && show_message ){
                                        User user = new User(full_name,phone_number,pw,sn);
                                        String label = user.getUser_phone_number();
                                        db.child(label).setValue(user);
                                        editText_full_name.setText(null);
                                        editText_phone_number.setText(null);
                                        editText_password.setText(null);
                                        editText_repassword.setText(null);
                                        editText_shop_name.setText(null);
                                        Toast.makeText(register.this,"Registered ! You can log in now.",Toast.LENGTH_LONG).show();
                                        checked =true;
                                        show_message=false;
                                        finish();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }
                    }
                }
        );
    }
}

