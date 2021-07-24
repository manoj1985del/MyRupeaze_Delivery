package com.texpediscia.myrupeazedelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.texpediscia.myrupeazedelivery.model.Seller;
import com.texpediscia.myrupeazedelivery.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterDeliveryListing extends RecyclerView.Adapter<AdapterDeliveryListing.ViewHolder> {

    private List<DeliveryListing> m_lstCustomers;
    private Context m_Context;

    IDeliveryOperations mOperation;

    public AdapterDeliveryListing(List<DeliveryListing> lstCustomers, Context ctx, IDeliveryOperations operations){
        m_lstCustomers = lstCustomers;
        m_Context = ctx;
        mOperation = operations;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_delivery_listing, parent, false);
        return new ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryListing listItem = m_lstCustomers.get(position);
        User customer = listItem.getmUser();

        holder.txtName.setText(customer.Name);
        holder.txtAddressLine1.setText(customer.AddressLine1);
        holder.txtAddressLine2.setText(customer.AddressLine2);
        holder.txtAddressLine3.setText(customer.AddressLine3);
        holder.txtPincode.setText(customer.Pincode);
        holder.txtContactNumber.setText(customer.Phone);
        holder.txtLandmark.setText(customer.Landmark);
        holder.txtCity.setText(customer.City);
        holder.txtState.setText(customer.State);
        holder.txtTotalOrders.setText("Total Orders: " + Integer.toString(customer.ordersList.size()));

    }

    @Override
    public int getItemCount() {
        return m_lstCustomers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtName;
        public TextView txtAddressLine1;
        public TextView txtAddressLine2;
        public TextView txtAddressLine3;
        public TextView txtLandmark;
        public TextView txtPincode;
        public TextView txtCity;
        public TextView txtState;
        public TextView txtContactNumber;
        public TextView txtTotalOrders;

        public Button btnMakeDefaultAddress;


        private Context m_ctx;
        public ViewHolder(@NonNull View itemView, Context m_Context) {
            super(itemView);
            m_ctx = m_Context;
            txtName = (TextView) itemView.findViewById(R.id.txtName_Address);
            txtAddressLine1 = (TextView) itemView.findViewById(R.id.txtAddresLine1_Address);
            txtAddressLine2 = (TextView) itemView.findViewById(R.id.txtAddresLine2_Address);
            txtAddressLine3 = (TextView) itemView.findViewById(R.id.txtAddresLine3_Address);
            txtLandmark = (TextView) itemView.findViewById(R.id.txtLandmark_Address);
            txtPincode = (TextView) itemView.findViewById(R.id.txtPincode_Address);
            txtCity = (TextView) itemView.findViewById(R.id.txtCity_Address);
            txtState = (TextView) itemView.findViewById(R.id.txtState_Address);
            txtContactNumber = (TextView) itemView.findViewById(R.id.txtContact_Address);
            txtTotalOrders = (TextView) itemView.findViewById(R.id.txtTotalOrders);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION){
                DeliveryListing deliveryListing = m_lstCustomers.get(pos);
                User user = deliveryListing.getmUser();
                mOperation.selectCustomer(user);

            }

        }
    }
}
