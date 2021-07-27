package com.texpediscia.myrupeazedelivery.ui.awaitingsellers;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.texpediscia.myrupeazedelivery.AdapterDeliveryListing;
import com.texpediscia.myrupeazedelivery.AdapterSellerListing;
import com.texpediscia.myrupeazedelivery.AwaitingOrders;
import com.texpediscia.myrupeazedelivery.CommonVariables;
import com.texpediscia.myrupeazedelivery.DeliveryListing;
import com.texpediscia.myrupeazedelivery.ISellerOperations;
import com.texpediscia.myrupeazedelivery.R;
import com.texpediscia.myrupeazedelivery.SellerListing;
import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.Seller;
import com.texpediscia.myrupeazedelivery.model.User;

import java.security.cert.CertPathValidatorException;
import java.util.ArrayList;
import java.util.List;

public class AwaitingSellers extends Fragment implements ISellerOperations {

    private AwaitingSellersViewModel mViewModel;

    private View mView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifView;

    private List<SellerListing> listItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Seller> sellerList = new ArrayList<>();
    ISellerOperations event;

    private List<Orders> orderList = new ArrayList<>();
    private int mRejectionOrderCount = 0;
    private int mRejectionIndex = 0;

    public static AwaitingSellers newInstance() {
        return new AwaitingSellers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.awaiting_sellers_fragment, container, false);
        checkPermission();
        event = this;
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AwaitingSellersViewModel.class);
        // TODO: Use the ViewModel

        recyclerView = (RecyclerView) mView.findViewById(R.id.sellersRecycler);
        gifView = mView.findViewById(R.id.gifView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        listItems.clear();
        sellerList.clear();
        orderList.clear();

        loadSellersForPendingOrders();


    }


//    int index = 0;
//    private void loadSellersForPendingOrders(){
//
//        index = 0;
//        db.collection("orders")
//                .whereEqualTo("delivery_agent_id", CommonVariables.loggedInUserDetails.customer_id)
//                .whereEqualTo("Status", "Delivery Agent Assigned")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            int iSize = task.getResult().size();
//
//                            if(iSize == 0){
//                                TextView txtErrorMsg = mView.findViewById(R.id.txtErrorMsg);
//                                txtErrorMsg.setVisibility(View.VISIBLE);
//                                gifView.setVisibility(View.GONE);
//                                return;
//                            }
//
//                            boolean isLast = false;
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                index++;
//                                //  Log.d(TAG, document.getId() + " => " + document.getData());
//                                Orders order = document.toObject(Orders.class);
//                                if(index == iSize){
//                                    isLast = true;
//                                }
//                                mapSellerAndOrder(order, isLast);
//                            }
//                        } else {
//                            // Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//    }
//
//    private void mapSellerAndOrder(final Orders order, final boolean isLast){
//
//        DocumentReference docRef = db.collection("seller").document(order.seller_id);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Seller seller = document.toObject(Seller.class);
//                        boolean sellerFound = false;
//                        for (Seller sel: sellerList) {
//                            if(sel.seller_id.equals(seller.seller_id)){
//                                seller = sel;
//                                sellerFound = true;
//                                break;
//                            }
//                        }
//                        if(sellerFound == false){
//                            sellerList.add(seller);
//                        }
//                        seller.ordersList.add(order);
//                        order.seller = seller;
//
//                        if(isLast){
//
//                            for (Seller sel : sellerList) {
//
//                                SellerListing item = new SellerListing(sel);
//                                listItems.add(item);
//                            }
//                            //  Toast.makeText(mView.getContext(), Integer.toString(idx), Toast.LENGTH_SHORT).show();
//                            adapter = new AdapterSellerListing(listItems, mView.getContext(), event);
//                            recyclerView.setAdapter(adapter);
//
//                            gifView.setVisibility(View.GONE);
//
//                        }
//                    } else {
//                        Log.d("TAG", "No such document");
//                    }
//                } else {
//                    Log.d("TAG", "get failed with ", task.getException());
//                }
//            }
//        });
//
//
//
//    }


    private void loadSellersForPendingOrders(){

        listItems.clear();
        sellerList.clear();
        indexSeller = 0;
        db.collection("orders")
                .whereEqualTo("delivery_agent_id", CommonVariables.loggedInUserDetails.customer_id)
                .whereEqualTo("Status", "Delivery Agent Assigned")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int iSize = task.getResult().size();
                            //Toast.makeText(m_Context, "Doc Size - " + Integer.toString(iSize), Toast.LENGTH_SHORT).show();
                            if(iSize == 0){
                                TextView txtErrorMsg = mView.findViewById(R.id.txtErrorMsg);
                                txtErrorMsg.setVisibility(View.VISIBLE);
                                gifView.setVisibility(View.GONE);
                                return;
                            }
                            int index = 0;
                            boolean isLast = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                index++;
                                //  Log.d(TAG, document.getId() + " => " + document.getData());
                                Orders order = document.toObject(Orders.class);
                                orderList.add(order);
                                if(index == iSize){


                                    mapSellerAndOrder(orderList);



                                }
                                //Toast.makeText(m_Context, "Is Last - " + Boolean.toString(isLast), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    int indexSeller = 0;
    private void mapSellerAndOrder(final List<Orders> orderList){


        for (Orders order : orderList) {
            final Orders _order = order;
            DocumentReference docRef = db.collection("seller").document(order.seller_id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            indexSeller++;
                            Seller seller = document.toObject(Seller.class);
                            boolean userFound = false;
                            for (Seller u : sellerList) {
                                if (u.seller_id.equals(seller.seller_id)) {
                                    seller = u;
                                    userFound = true;
                                    break;
                                }
                            }

                            seller.ordersList.add(_order);
                            if (userFound == false) {
                               // Log.d("Manoj", user.customer_id);
                                sellerList.add(seller);
                            }


                            if (indexSeller == orderList.size()) {
                                //Toast.makeText(m_Context, "Customer size - " + Integer.toString(customerList.size()) + " order list size -" + orderList.size(), Toast.LENGTH_SHORT).show();

                               // Log.d("Manoj", "Customer Size" + Integer.toString(customerList.size())+  "idx = " + Integer.toString(indexSeller));
                                for (Seller sel : sellerList) {

                                    SellerListing item = new SellerListing(sel);
                                    listItems.add(item);
                                }
                                //  Toast.makeText(mView.getContext(), Integer.toString(idx), Toast.LENGTH_SHORT).show();
                                adapter = new AdapterSellerListing(listItems, mView.getContext(), event);
                                recyclerView.setAdapter(adapter);

                                gifView.setVisibility(View.GONE);

                            }
                        } else
                            Log.d("TAG", "No such document");
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });

        }



    }


    @Override
    public void SelectSeller(Seller seller) {

        Intent intent = new Intent(getActivity(), AwaitingOrders.class);
        intent.putExtra("Seller", seller);
        startActivity(intent);

        //Toast.makeText(mView.getContext(), seller.seller_id, Toast.LENGTH_SHORT ).show();
//        Intent intent = new Intent(AwaitingSellers.this, AwaitingOrders.class);
//                intent.putExtra("employee", employee);
//                m_ctx.startActivity(intent);

    }


    @Override
    public void RejectOrders(Seller seller, String reason) {

        mRejectionOrderCount = seller.ordersList.size();
        mRejectionIndex = 0;
        for (Orders ord: seller.ordersList) {
            MarkRejectionInDb(ord, reason);
        }
    }

    private void MarkRejectionInDb(Orders order, String sReason){

        DocumentReference washingtonRef = db.collection("orders").document(order.order_id);
        // Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update(
                        "Status", "Order Rejected by Local Delivery Agent - " + CommonVariables.loggedInUserDetails.Name + ", Another Agent will be Assigned shortly.",
                        "delivery_agent_id", null,
                        "pickup_status", "rejected",
                        "pickup_rejection_reason", sReason
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRejectionIndex++;
                        if(mRejectionIndex == mRejectionOrderCount)
                        {
                            Toast.makeText(mView.getContext(), "Orders Rejection Marked Successfully", Toast.LENGTH_SHORT).show();
                            loadSellersForPendingOrders();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 1);
        }
    }
    
}
