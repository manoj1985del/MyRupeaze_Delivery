package com.texpediscia.myrupeazedelivery.ui.awaitingmedical;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.texpediscia.myrupeazedelivery.AdapterPharmacistListing;
import com.texpediscia.myrupeazedelivery.AwaitingMedicalOrders;
import com.texpediscia.myrupeazedelivery.CommonVariables;
import com.texpediscia.myrupeazedelivery.ISellerOperations;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;
import com.texpediscia.myrupeazedelivery.R;
import com.texpediscia.myrupeazedelivery.SellerListing;
import com.texpediscia.myrupeazedelivery.model.Seller;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class AwaitingMedicalPickupsFragment extends Fragment implements ISellerOperations{

    private View root;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifView;
    private List<PharmacistRequests> requestsList;
    private List<SellerListing> listItems;
    private RecyclerView recyclerView;
    private TextView txtErrorMsg;
    private AdapterPharmacistListing adapter;
    private List<Seller> sellerList;
    ISellerOperations event;
    int indexSeller = 0;
    private int mRejectionOrderCount = 0;
    private int mRejectionIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_awaiting_medical_pickups, container, false);

        event = this;

        listItems = new ArrayList<>();
        requestsList = new ArrayList<>();
        sellerList = new ArrayList<>();
        txtErrorMsg = root.findViewById(R.id.txtErrorMsg);
        recyclerView =  root.findViewById(R.id.sellersRecycler);
        gifView = root.findViewById(R.id.gifView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        loadSellersForPendingOrders();

        return root;
    }

    private void loadSellersForPendingOrders(){

        //status code 7: Delivery Agent Assigned
        //status code 8: Picked up by local delivery agent for delivery

        requestsList.clear();
        sellerList.clear();
        indexSeller = 0;
        db.collection("pharmacist_requests")
                .whereEqualTo("delivery_agent_id", CommonVariables.loggedInUserDetails.customer_id)
                .whereEqualTo("status_code", 7)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int iSize = task.getResult().size();
                            if(iSize == 0){
                                txtErrorMsg.setVisibility(View.VISIBLE);
                                gifView.setVisibility(View.GONE);
                                return;
                            }
                            int index = 0;
                            boolean isLast = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                index++;
                                PharmacistRequests pharmacistRequests = document.toObject(PharmacistRequests.class);
                                requestsList.add(pharmacistRequests);
                                if(index == iSize){

                                    mapSellerAndOrder(requestsList);

                                }
                            }
                        } else {
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



    }

    private void mapSellerAndOrder(final List<PharmacistRequests> requestsList){


        for (PharmacistRequests requests : requestsList) {
            final PharmacistRequests request = requests;
            DocumentReference docRef = db.collection("seller").document(request.seller_id);
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
                            seller.pharmacistRequestsList.add(request);
                            if (!userFound) {
                                sellerList.add(seller);
                            }


                            if (indexSeller == requestsList.size()) {
                                for (Seller sel : sellerList) {
                                    SellerListing item = new SellerListing(sel);
                                    listItems.add(item);
                                }
                                adapter = new AdapterPharmacistListing(listItems, root.getContext(), event);
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

    private void MarkRejectionInDb(PharmacistRequests requests, String sReason){

        DocumentReference washingtonRef = db.collection("pharmacist_requests").document(requests.doc_id);
        washingtonRef
                .update(
                        "status_code", 6,
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
                            Toast.makeText(root.getContext(), "Orders Rejection Marked Successfully", Toast.LENGTH_SHORT).show();
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


    @Override
    public void SelectSeller(Seller seller) {
        CommonVariables.selectedSeller = seller;
        Intent intent = new Intent(getActivity(), AwaitingMedicalOrders.class);
       // intent.putExtra("Seller", seller);
        startActivity(intent);

    }

    @Override
    public void RejectOrders(Seller seller, String reason) {
        mRejectionOrderCount = seller.pharmacistRequestsList.size();
        mRejectionIndex = 0;
        for (PharmacistRequests requests: seller.pharmacistRequestsList) {
            MarkRejectionInDb(requests, reason);
        }
    }
}