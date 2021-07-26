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

import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;

import java.util.List;

public class AdapterMedicalOrderDelivery extends RecyclerView.Adapter<AdapterMedicalOrderDelivery.ViewHolder> {

    private List<OrdersForDeliveryListing> m_lstOrders;
    private Context m_Context;
    private IDeliverMedicalOrders mOperations;

    public AdapterMedicalOrderDelivery(List<OrdersForDeliveryListing> lstOrders, Context ctx, IDeliverMedicalOrders operations){
        m_lstOrders = lstOrders;
        m_Context = ctx;
        mOperations = operations;
    }


    @NonNull
    @Override
    public AdapterMedicalOrderDelivery.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_orders_for_delivery_listing, parent, false);
        return new AdapterMedicalOrderDelivery.ViewHolder(v, m_Context);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMedicalOrderDelivery.ViewHolder holder, int position) {

        OrdersForDeliveryListing listItem = m_lstOrders.get(position);

        PharmacistRequests pharmacistRequests = listItem.getPharmacistRequests();

        holder.txtOrderId.setText("Order Id: " + pharmacistRequests.doc_id);

    }

    @Override
    public int getItemCount() {
        return m_lstOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView txtOrderId;
        public Button btnMarkDelivery;
        public Button btnMarkRejection;
        public LinearLayout layoutRejectionReason;
        public Button btnSubmitRejection;
        public EditText txtRejectionReason;


        public Button btnMakeDefaultAddress;


        private Context m_ctx;
        public ViewHolder(@NonNull View itemView, Context m_Context) {
            super(itemView);
            m_ctx = m_Context;
            txtOrderId = (TextView) itemView.findViewById(R.id.txtOrderId);
            btnMarkDelivery = (Button)itemView.findViewById(R.id.btnMarkDelivery);
            btnMarkRejection = (Button) itemView.findViewById(R.id.btnMarkRejection);
            layoutRejectionReason = (LinearLayout)itemView.findViewById(R.id.layoutRejectionReason);

            btnSubmitRejection = (Button)itemView.findViewById(R.id.btnSubmitRejection);
            txtRejectionReason = (EditText)itemView.findViewById(R.id.txtRejectionReason);

            btnMarkDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        OrdersForDeliveryListing orderListing = m_lstOrders.get(pos);
                        PharmacistRequests requests = orderListing.getPharmacistRequests();
                        mOperations.DeliverOrder(requests);
                    }
                }
            });

            btnMarkRejection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnMarkDelivery.setVisibility(View.GONE);
                    btnMarkRejection.setVisibility(View.GONE);
                    layoutRejectionReason.setVisibility(View.VISIBLE);
                }
            });

            btnSubmitRejection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        OrdersForDeliveryListing orderListing = m_lstOrders.get(pos);
                        PharmacistRequests requests = orderListing.getPharmacistRequests();
                        mOperations.RejectOrder(requests , txtRejectionReason.getText().toString());
                    }
                }
            });


        }
    }
}

