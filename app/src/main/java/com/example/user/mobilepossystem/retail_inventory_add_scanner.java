package com.example.user.mobilepossystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class retail_inventory_add_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    private String scanner_code,session_name;;
    private  static final int requestCode=1;
    private int selected_quantity=1,new_quantity;
    private boolean checked = false,existed = false;
    private Product po;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_inventory_add_scanner);

        Intent i =  getIntent();
        scanner_code = i.getExtras().getString("Use Scanner");
        session_name = i.getExtras().getString("Session ID");
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);
        //check and request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, requestCode);
        }
    }

    @Override
    public void handleResult(Result result) {
        if(scanner_code.equals("Inventory Retail")){
            retail_inventory_add.bc.setText(result.getText());
        }else if(scanner_code.equals("Retail Menu")){
            addItemtoOrder(result.getText());
        }
        onBackPressed();
    }

    private void addItemtoOrder(final String r) {
        DatabaseReference d = FirebaseDatabase.getInstance().getReference("product").child(session_name).child("Retail");
        d.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Product p = d.getValue(Product.class);
                    if(r.equals(p.getBarcode())){
                        checked = true;
                        po = new Product(p.getBarcode(),p.getProduct_name(),p.getCategory(),p.getPrice(),p.getQuantity());
                        break;
                    }
                }
                if(checked){
                    final DatabaseReference d_o = FirebaseDatabase.getInstance().getReference("order").child(session_name).child("Retail");
                    d_o.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                Product pp = ds.getValue(Product.class);
                                if(pp.getProduct_name().equals(po.getProduct_name())){
                                    new_quantity = Integer.parseInt(pp.getQuantity());
                                    existed = true;
                                    break;
                                }
                            }
                            if(existed){
                                Product p_o = new Product(po.getProduct_name(),po.getPrice(),String.valueOf(new_quantity+selected_quantity));
                                d_o.child(po.getProduct_name()).setValue(p_o);
                                existed=false;
                                Toast.makeText(retail_inventory_add_scanner.this, po.getProduct_name()+"'s quantity updated", Toast.LENGTH_SHORT).show();
                            }else{
                                Product p_o = new Product(po.getProduct_name(),po.getPrice(),String.valueOf(selected_quantity));
                              d_o.child(po.getProduct_name()).setValue(p_o);
                                selected_quantity = 1;
                                Toast.makeText(retail_inventory_add_scanner.this, po.getProduct_name()+"' is added!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    checked=false;
                }else{
                    Toast.makeText(retail_inventory_add_scanner.this, "Item doesn't exist.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}
