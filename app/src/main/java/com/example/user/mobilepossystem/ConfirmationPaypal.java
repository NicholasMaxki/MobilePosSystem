package com.example.user.mobilepossystem;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONObject;
import org.json.JSONException;
public class ConfirmationPaypal extends AppCompatActivity {
private  String session_name,shop_name,un,order_id,p_type,date_time,payment_ID ="";
private  double change,sub_total,grand_total,discount_rate,cash;
private Button btn_proceed;
private  int discount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_paypal);

        Intent i = getIntent();
        session_name = i.getExtras().getString("Session ID");
        shop_name = i.getExtras().getString("Shop");
        un = i.getExtras().getString("UN");
        date_time = i.getExtras().getString("Date Time");
        order_id = i.getExtras().getString("Order ID");
        change = i.getExtras().getDouble("Change");
        sub_total = i.getExtras().getDouble("Sub Total");
        grand_total = i.getExtras().getDouble("Grand Total");
        discount = i.getExtras().getInt("Discount");
        discount_rate = i.getExtras().getDouble("Discount rate");
        cash = i.getExtras().getDouble("Cash");
        p_type = i.getExtras().getString("Payment Type");

        btn_proceed = (Button)findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });
        Intent intent = getIntent();
        try {
            JSONObject jsonDetails = new JSONObject(intent.getStringExtra("PaymentDetails"));

            //Displaying payment details 
            showDetails(jsonDetails.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void proceed() {
        Intent myIntent = new Intent(ConfirmationPaypal.this, retail_payment_receipt.class);
        myIntent.putExtra("Session ID",session_name);
        myIntent.putExtra("Shop",shop_name);
        myIntent.putExtra("Date Time",date_time);
        myIntent.putExtra("Order ID",order_id);
        myIntent.putExtra("Change",0);
        myIntent.putExtra("Sub Total",sub_total);
        myIntent.putExtra("Grand Total",grand_total);
        myIntent.putExtra("Discount",discount);
        myIntent.putExtra("Discount rate",discount_rate);
        myIntent.putExtra("Cash",0);
        myIntent.putExtra("Payment Type",p_type);
        myIntent.putExtra("PID",payment_ID);
        myIntent.putExtra("UN",un);
        startActivity(myIntent);
        stopService(new Intent(this, PayPalService.class));
        finish();
    }
    private void showDetails(JSONObject jsonDetails, String paymentAmount) throws JSONException {
        //Views
        TextView textViewId = (TextView) findViewById(R.id.paymentId);
        TextView textViewStatus= (TextView) findViewById(R.id.paymentStatus);
        TextView textViewAmount = (TextView) findViewById(R.id.paymentAmount);
        payment_ID = jsonDetails.getString("id");
        //Showing the details from json object
        textViewId.setText(jsonDetails.getString("id"));
        textViewStatus.setText(jsonDetails.getString("state"));
        textViewAmount.setText("MYR "+paymentAmount);

    }
    @Override
    public void onBackPressed() { }
}

