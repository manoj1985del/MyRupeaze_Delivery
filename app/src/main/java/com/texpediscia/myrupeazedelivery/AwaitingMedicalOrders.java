package com.texpediscia.myrupeazedelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;
import com.texpediscia.myrupeazedelivery.model.Seller;
import com.texpediscia.myrupeazedelivery.model.User;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class AwaitingMedicalOrders extends AppCompatActivity {

    private List<AwaitingOrderListing> listItems;
    private RecyclerView recyclerView;
    private AdapterAwaitingMedicalOrders adapter;
    GifImageView gifView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Seller mSeller;

    private Context m_Context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awaiting_orders);

        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.orderRecycler);
        gifView = findViewById(R.id.gifView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_Context = this;

//        Intent iin = getIntent();
//        Bundle b = iin.getExtras();
//
//        if (b != null) {
//            mSeller = (Seller) b.get("Seller");
//        }

        mSeller = CommonVariables.selectedSeller;

        loadCustomerForPendingOrder();
        final Activity activity = this;

        Button btnScan = findViewById(R.id.btnScan);
        TextView txtErrorMsg = findViewById(R.id.txtErrorMsg);
        if (mSeller.pharmacistRequestsList.size() == 0) {
            btnScan.setVisibility(View.GONE);

            txtErrorMsg.setVisibility(View.VISIBLE);
        } else {
            txtErrorMsg.setVisibility(View.GONE);
        }

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(activity).initiateScan(); // `this` is the current Activity

            }
        });


    }

    private void loadCustomerForPendingOrder() {

        for (int i = 0; i < mSeller.pharmacistRequestsList.size(); i++) {
            PharmacistRequests request = mSeller.pharmacistRequestsList.get(i);
            String customerId = request.customer_id;
            boolean isLast = false;
            if (i == mSeller.pharmacistRequestsList.size() - 1) {
                isLast = true;
            }
            mapCustomerAndOrder(request, isLast);
        }
    }

    private void mapCustomerAndOrder(final PharmacistRequests requests, final boolean isLast) {

        DocumentReference docRef = db.collection("users").document(requests.customer_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        requests.user = user;

                        if (isLast) {

                            for (PharmacistRequests pharmacistRequests : mSeller.pharmacistRequestsList) {

                                AwaitingOrderListing item = new AwaitingOrderListing(pharmacistRequests);
                                listItems.add(item);
                            }
                            //  Toast.makeText(mView.getContext(), Integer.toString(idx), Toast.LENGTH_SHORT).show();
                            adapter = new AdapterAwaitingMedicalOrders(listItems, m_Context);
                            recyclerView.setAdapter(adapter);

                            gifView.setVisibility(View.GONE);

                        }
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String scannedBarcode = result.getContents();
                boolean orderFound = false;
                int foundIndex = -1;
                for (PharmacistRequests requests : mSeller.pharmacistRequestsList) {
                    foundIndex++;
                    if (requests.doc_id.equals(scannedBarcode)) {
                        orderFound = true;
                        pickupForDelivery(requests);
                        Toast.makeText(this, "Order Picked up for delivery", Toast.LENGTH_LONG).show();
                        break;
                    }

                }

                if (orderFound) {
                    mSeller.pharmacistRequestsList.remove(foundIndex);
                    listItems.clear();
                    loadCustomerForPendingOrder();
                }
                if (!orderFound) {
                    Toast.makeText(this, "Order not found / Invalid Order", Toast.LENGTH_LONG).show();
                }
                // Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void pickupForDelivery(PharmacistRequests request) {
        DocumentReference washingtonRef = db.collection("pharmacist_requests").document(request.doc_id);
        washingtonRef
                .update("status_code", 8)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

}
