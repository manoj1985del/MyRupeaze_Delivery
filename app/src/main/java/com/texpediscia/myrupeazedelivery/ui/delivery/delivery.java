package com.texpediscia.myrupeazedelivery.ui.delivery;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
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
import com.texpediscia.myrupeazedelivery.IDeliveryOperations;
import com.texpediscia.myrupeazedelivery.ISellerOperations;
import com.texpediscia.myrupeazedelivery.OrdersForDeliveryListing;
import com.texpediscia.myrupeazedelivery.R;
import com.texpediscia.myrupeazedelivery.SellerListing;
import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.Seller;
import com.texpediscia.myrupeazedelivery.model.User;

import java.util.ArrayList;
import java.util.List;

public class delivery extends Fragment implements IDeliveryOperations {

    private DeliveryViewModel mViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View mView;
    private GifImageView gifView;

    private List<DeliveryListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<User> customerList = new ArrayList<>();
    private List<Orders> orderList = new ArrayList<>();

    private Context m_Context;

    IDeliveryOperations event;

    public static delivery newInstance() {
        return new delivery();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.delivery_fragment, container, false);
        event = this;
        m_Context = mView.getContext();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DeliveryViewModel.class);
        // TODO: Use the ViewModel


    }

    @Override
    public void onStart() {
        super.onStart();

        listItems = new ArrayList<>();
        orderList = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.deliveryRecycler);
        gifView = mView.findViewById(R.id.gifView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        loadCustomersForPendingOrders();
    }

    private void loadCustomersForPendingOrders(){

        listItems.clear();
        customerList.clear();
        indexSeller = 0;
        db.collection("orders")
                .whereEqualTo("delivery_agent_id", CommonVariables.loggedInUserDetails.customer_id)
                .whereEqualTo("Status", "Picked up by local delivery agent for delivery")
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
            DocumentReference docRef = db.collection("users").document(order.customer_id);
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

                            user.ordersList.add(_order);
                            if (userFound == false) {
                                Log.d("Manoj", user.customer_id);
                                customerList.add(user);
                            }


                        if (indexSeller == orderList.size()) {
                                //Toast.makeText(m_Context, "Customer size - " + Integer.toString(customerList.size()) + " order list size -" + orderList.size(), Toast.LENGTH_SHORT).show();

                                Log.d("Manoj", "Customer Size" + Integer.toString(customerList.size())+  "idx = " + Integer.toString(indexSeller));
                                for (User customer : customerList) {

                                    DeliveryListing item = new DeliveryListing(customer);
                                    listItems.add(item);
                                }
                                //  Toast.makeText(mView.getContext(), Integer.toString(idx), Toast.LENGTH_SHORT).show();
                                adapter = new AdapterDeliveryListing(listItems, mView.getContext(), event);
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
        Fragment newFragment = OrdersForDelivery.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
