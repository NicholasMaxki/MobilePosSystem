package com.example.user.mobilepossystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile extends AppCompatActivity {
private EditText fname,pw,pw2,shop;
private String session_name,shop_name,un,mode;
private Button btn_update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        mode = i.getExtras().getString("Mode","Restaurant");

        fname = findViewById(R.id.fname);
        fname.setText(un);
        shop=findViewById(R.id.shop);
        shop.setText(shop_name);
        pw=findViewById(R.id.pw);
        pw2=findViewById(R.id.pw2);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user").child(session_name);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    User u = dataSnapshot.getValue(User.class);
                    pw.setText(u.getUser_password());
                    pw2.setText(u.getUser_password());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btn_update=findViewById(R.id.update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

    }

    private void updateProfile() {
        if(TextUtils.isEmpty(fname.getText())){
            Toast.makeText(profile.this,"Full name can't be blanked !",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(shop.getText())){
            Toast.makeText(profile.this,"Shop name can't be blanked !",Toast.LENGTH_LONG).show();;
        }
        else if(TextUtils.isEmpty(pw.getText())){
            Toast.makeText(profile.this,"Password can't be blanked!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(pw2.getText())){
            Toast.makeText(profile.this,"Please enter again your password !",Toast.LENGTH_LONG).show();;
        }
        else if(!pw.getText().toString().equals(pw2.getText().toString())){
            Toast.makeText(profile.this,"Your password is not matched !",Toast.LENGTH_LONG).show();;
        }else{
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("user").child(session_name);
            User u = new User(fname.getText().toString(),session_name,pw.getText().toString(),shop.getText().toString());
            db.setValue(u);
            shop_name = shop.getText().toString();
            un=fname.getText().toString();
            if(mode.equals("Restaurant")){
                Intent myIntent = new Intent(profile.this, restaurant_take_order.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
                finish();
            }else{
                Intent myIntent = new Intent(profile.this, retail_main_menu.class);
                myIntent.putExtra("Session ID",session_name);
                myIntent.putExtra("Shop",shop_name);
                myIntent.putExtra("UN",un);
                startActivity(myIntent);
                finish();
            }
        }
    }
}
