package com.example.user.mobilepossystem;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class system_main_menu extends AppCompatActivity {
    ImageButton btn_retail,btn_restaurant;
    private String session_name,shop_name,un;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");

        setContentView(R.layout.activity_system_main_menu);
        btn_retail=(ImageButton) findViewById(R.id.retail);
        btn_restaurant=(ImageButton)findViewById(R.id.restaurant);
        goto_Restaurant();
        goto_Retail();
    }
    public void goto_Restaurant(){
    btn_restaurant.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(system_main_menu.this, restaurant_role.class);
                    myIntent.putExtra("Session ID",session_name);
                    myIntent.putExtra("Shop",shop_name);
                    myIntent.putExtra("UN",un);
                    startActivity(myIntent);
                }
            }
    );
}
    public void goto_Retail(){
    btn_retail.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(system_main_menu.this, retail_main_menu.class);
                    myIntent.putExtra("Session ID",session_name);
                    myIntent.putExtra("Shop",shop_name);
                    myIntent.putExtra("UN",un);
                    startActivity(myIntent);

                }
            }
    );
}
}
