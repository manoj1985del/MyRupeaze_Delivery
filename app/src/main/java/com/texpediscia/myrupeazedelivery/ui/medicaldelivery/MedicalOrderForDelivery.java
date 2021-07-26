package com.texpediscia.myrupeazedelivery.ui.medicaldelivery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.texpediscia.myrupeazedelivery.AdapterMedicalOrderDelivery;
import com.texpediscia.myrupeazedelivery.AdapterOrdersForDelivery;
import com.texpediscia.myrupeazedelivery.CommonVariables;
import com.texpediscia.myrupeazedelivery.IDeliverMedicalOrders;
import com.texpediscia.myrupeazedelivery.IDeliverOrders;
import com.texpediscia.myrupeazedelivery.OrdersForDeliveryListing;
import com.texpediscia.myrupeazedelivery.R;
import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;
import com.texpediscia.myrupeazedelivery.model.User;
import com.texpediscia.myrupeazedelivery.ui.delivery.OrdersForDelivery;
import com.texpediscia.myrupeazedelivery.ui.delivery.OrdersForDeliveryViewModel;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MedicalOrderForDelivery extends Fragment implements IDeliverMedicalOrders {

    private View mView;
    private List<OrdersForDeliveryListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifView;
    private User mCustomer;
    IDeliverMedicalOrders event;

    public static MedicalOrderForDelivery newInstance() {
        return new MedicalOrderForDelivery();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_medical_order_for_delivery, container, false);
        event = this;
        return  mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.orderRecycler);
        gifView = mView.findViewById(R.id.gifView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        if(CommonVariables.selectedUser == null){
            return;
        }
        else{
            mCustomer = CommonVariables.selectedUser;
            loadOrders();
        }

    }

    private void loadOrders(){
        listItems = new ArrayList<>();
        for (PharmacistRequests requests : mCustomer.pharmacistRequestsList) {

            OrdersForDeliveryListing item = new OrdersForDeliveryListing(requests);
            listItems.add((item));
        }

        adapter = new AdapterMedicalOrderDelivery(listItems, mView.getContext(), event);
        recyclerView.setAdapter(adapter);
        gifView.setVisibility(View.GONE);

    }

    @Override
    public void DeliverOrder(final PharmacistRequests pharmacistRequest) {

        DocumentReference washingtonRef = db.collection("pharmacist_requests").document(pharmacistRequest.doc_id);

        washingtonRef
                .update(
                        "status_code", 5,
                        "delivery_date", FieldValue.serverTimestamp()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int index = 0;
                        for (PharmacistRequests requests: mCustomer.pharmacistRequestsList) {
                            if(requests.doc_id.equals(pharmacistRequest.doc_id)){
                                break;
                            }
                            index++;
                        }
                        mCustomer.pharmacistRequestsList.remove(index);
                        loadOrders();
                        Toast.makeText(mView.getContext(), "Order delivery successful", Toast.LENGTH_SHORT).show();

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

    @Override
    public void RejectOrder(final PharmacistRequests requests, final String reason) {
        DocumentReference washingtonRef = db.collection("pharmacist_requests").document(requests.doc_id);

// Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update(
                        "status_code", 4,
                        "cancellation_reason", reason,
                        "pickup_status", "attempted delivery failed"
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int index = 0;
                        for (PharmacistRequests request: mCustomer.pharmacistRequestsList) {
                            if(request.doc_id.equals(requests.doc_id)){
                                break;
                            }
                            index++;
                        }
                        mCustomer.pharmacistRequestsList.remove(index);
                        loadOrders();
                        Toast.makeText(mView.getContext(), "Order Cancellation successful. Reason : " + reason, Toast.LENGTH_SHORT).show();

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
