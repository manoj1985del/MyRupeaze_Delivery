package com.texpediscia.myrupeazedelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.texpediscia.myrupeazedelivery.model.Seller;

import java.util.List;

public class AdapterPharmacistListing extends RecyclerView.Adapter<AdapterPharmacistListing.ViewHolder> {

    private List<SellerListing> m_lstSellers;
    private Context m_Context;
    ISellerOperations mSellerOperations;

    public AdapterPharmacistListing(List<SellerListing> lstSellers, Context ctx, ISellerOperations sellerOperations) {
        m_lstSellers = lstSellers;
        m_Context = ctx;
        mSellerOperations = sellerOperations;
    }

    @NonNull
    @Override
    public AdapterPharmacistListing.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_seller_listing, parent, false);
        return new AdapterPharmacistListing.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPharmacistListing.ViewHolder holder, int position) {

        SellerListing listItem = m_lstSellers.get(position);
        Seller seller = listItem.getSeller();

        String sTotalOrders = Integer.toString(seller.pharmacistRequestsList.size());

        holder.txtName.setText(seller.company_name + " (" + sTotalOrders + ")");
        holder.txtAddressLine1.setText(seller.address_line1);
        holder.txtAddressLine2.setText(seller.address_line2);
        holder.txtAddressLine3.setText(seller.address_line3);
        holder.txtPincode.setText(seller.pincode);
        holder.txtContactNumber.setText(seller.mobile);

        holder.txtLandmark.setVisibility(View.GONE);

        holder.txtCity.setText(seller.city);
        holder.txtState.setText(seller.state);
        String totalOrders = "Total Orders: " + seller.pharmacistRequestsList.size();
        holder.txtTotalOrders.setText(totalOrders);
    }

    @Override
    public int getItemCount() {
        return m_lstSellers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        public Button bttnRejectOrder;
        public LinearLayout layoutRejectionReason;

        public EditText txtRejectionReason;
        public Button btnSubmitRejection;

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

            bttnRejectOrder = (Button) itemView.findViewById(R.id.bttnRejectOrder);
            layoutRejectionReason = (LinearLayout) itemView.findViewById(R.id.layoutRejectionReason);
            txtRejectionReason = (EditText) itemView.findViewById(R.id.txtRejectionReason);
            btnSubmitRejection = (Button) itemView.findViewById(R.id.btnSubmitRejection);

            bttnRejectOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bttnRejectOrder.setVisibility(View.GONE);
                    layoutRejectionReason.setVisibility(View.VISIBLE);
                }
            });

            btnSubmitRejection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        SellerListing sellerListing = m_lstSellers.get(pos);
                        Seller seller = sellerListing.getSeller();
                        String sReason = txtRejectionReason.getText().toString();
                        mSellerOperations.RejectOrders(seller, sReason);

                    }

                }
            });


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                SellerListing sellerListing = m_lstSellers.get(pos);
                Seller seller = sellerListing.getSeller();
                mSellerOperations.SelectSeller(seller);

            }

        }
    }
}

