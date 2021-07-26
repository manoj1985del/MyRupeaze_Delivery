package com.texpediscia.myrupeazedelivery.ui.medicaldelivery;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.texpediscia.myrupeazedelivery.AdapterDeliveryListing;
import com.texpediscia.myrupeazedelivery.CommonVariables;
import com.texpediscia.myrupeazedelivery.DeliveryListing;
import com.texpediscia.myrupeazedelivery.IDeliveryOperations;
import com.texpediscia.myrupeazedelivery.R;
import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;
import com.texpediscia.myrupeazedelivery.model.User;
import com.texpediscia.myrupeazedelivery.ui.delivery.DeliveryViewModel;
import com.texpediscia.myrupeazedelivery.ui.delivery.OrdersForDelivery;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MedicalDeliveriesFragment extends Fragment implements IDeliveryOperations{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View root;
    private GifImageView gifView;

    private List<DeliveryListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<User> customerList = new ArrayList<>();
    private List<PharmacistRequests> pharmacistRequestsList = new ArrayList<>();

    private Context m_Context;

    IDeliveryOperations event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_medical_deliveries, container, false);
        event = this;
        m_Context = root.getContext();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) root.findViewById(R.id.deliveryRecycler);
        gifView = root.findViewById(R.id.gifView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        loadCustomersForPendingOrders();
    }

    private void loadCustomersForPendingOrders(){

        listItems.clear();
        customerList.clear();
        indexSeller = 0;
        db.collection("pharmacist_requests")
                .whereEqualTo("delivery_agent_id", CommonVariables.loggedInUserDetails.customer_id)
                .whereEqualTo("status_code", 8)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("value_here", "success");

                            int iSize = task.getResult().size();
                            //Toast.makeText(m_Context, "Doc Size - " + Integer.toString(iSize), Toast.LENGTH_SHORT).show();
                            if(iSize == 0){
                                Log.d("value_here", "empty");
                                TextView txtErrorMsg = root.findViewById(R.id.txtErrorMsg);
                                txtErrorMsg.setVisibility(View.VISIBLE);
                                gifView.setVisibility(View.GONE);
                                return;
                            }
                            int index = 0;
                            boolean isLast = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                index++;
                                //  Log.d(TAG, document.getId() + " => " + document.getData());
                                PharmacistRequests pharmacistRequest = document.toObject(PharmacistRequests.class);
                                pharmacistRequestsList.add(pharmacistRequest);
                                if(index == iSize){

                                    mapSellerAndOrder(pharmacistRequestsList);

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
    private void mapSellerAndOrder(final List<PharmacistRequests> pharmacistRequestsList){

        Log.d("value_here", "success 2");
        for (PharmacistRequests request : pharmacistRequestsList) {
            final PharmacistRequests _request = request;
            DocumentReference docRef = db.collection("users").document(request.customer_id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            indexSeller++;
                            User user = document.toObject(User.class);
                            boolean userFound = false;
                            for (User u : customerList) {
                                if (u.customer_id.equals(user.customer_id)) {
                                    user = u;
                                    userFound = true;
                                    break;
                                }
                            }

                            user.pharmacistRequestsList.add(_request);
                            if (!userFound) {
                                Log.d("Manoj", user.customer_id);
                                customerList.add(user);
                            }


                            if (indexSeller == pharmacistRequestsList.size()) {


                                Log.d("Manoj", "Customer Size" + Integer.toString(customerList.size())+  "idx = " + Integer.toString(indexSeller));
                                for (User customer : customerList) {

                                    DeliveryListing item = new DeliveryListing(customer);
                                    listItems.add(item);
                                }
                                //  Toast.makeText(root.getContext(), Integer.toString(idx), Toast.LENGTH_SHORT).show();
                                adapter = new AdapterDeliveryListing(listItems, root.getContext(), event);
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
    public void selectCustomer(User user) {

        CommonVariables.selectedUser = user;
        Fragment newFragment = MedicalOrderForDelivery.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}