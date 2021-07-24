package com.texpediscia.myrupeazedelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.Seller;
import com.texpediscia.myrupeazedelivery.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAwaitingOrders extends RecyclerView.Adapter<AdapterAwaitingOrders.ViewHolder> {

    private List<AwaitingOrderListing> m_lstOrders;
    private Context m_Context;

    public AdapterAwaitingOrders(List<AwaitingOrderListing> lstOrders, Context ctx){
        m_lstOrders = lstOrders;
        m_Context = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_awaiting_order_listing, parent, false);
        return new ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AwaitingOrderListing listItem = m_lstOrders.get(position);

        Orders order = listItem.getOrder();

        User user = order.user;


        holder.txtName.setText(user.Name);
        holder.txtAddressLine1.setText(user.AddressLine1);
        holder.txtAddressLine2.setText(user.AddressLine2);
        holder.txtAddressLine3.setText(user.AddressLine3);
        holder.txtPincode.setText(user.Pincode);
        holder.txtContactNumber.setText(user.Phone);

        holder.txtLandmark.setText(user.Landmark);

        holder.txtCity.setText(user.City);
        holder.txtState.setText(user.State);

        holder.txtOrderId.setText("Order Id: " + order.order_id);
    }

    @Override
    public int getItemCount() {
        return m_lstOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtName;
        public TextView txtAddressLine1;
        public TextView txtAddressLine2;
        public TextView txtAddressLine3;
        public TextView txtLandmark;
        public TextView txtPincode;
        public TextView txtCity;
        public TextView txtState;
        public TextView txtContactNumber;
        public TextView txtOrderId;


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
            txtOrderId = (TextView) itemView.findViewById(R.id.txtOrderId);
        }
    }
}
