package com.texpediscia.myrupeazedelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.texpediscia.myrupeazedelivery.model.Address;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.texpediscia.myrupeazedelivery.model.User;

import java.util.UUID;

import static android.view.View.GONE;

public class UserOtherDetails extends AppCompatActivity {

    private User currentUser;
    private FirebaseAuth mAuth;
    public String m_fcmToken;

    EditText txtName;
    EditText txtPhone;
    EditText txtEmail;
    RadioButton rbMale;
    EditText txtAdddressLine1;
    EditText txtAdddressLine2;
    EditText txtAdddressLine3 ;
    EditText txtLandmark;
    EditText txtPincode;
    EditText txtCity;
    EditText txtState;

    private String areaPin;



    private boolean m_bAddAddress = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_other_details);

        getFCMToken();

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            m_bAddAddress =(boolean) b.get("change_address");
        }


        txtName = findViewById(R.id.txtOhterDetailsName);
        if(!CommonVariables.UserName.equals("")){
            txtName.setText(CommonVariables.UserName);
        }

        txtPhone = findViewById(R.id.txtOtherDetailsPhone);
        if(!CommonVariables.Phone.equals("")){
            txtPhone.setText(CommonVariables.Phone);
        }

        txtEmail = findViewById(R.id.txtotherDetailsEmail);
        if(!CommonVariables.Email.equals("")){
            txtEmail.setText(CommonVariables.Email);
        }

        mAuth = FirebaseAuth.getInstance();

        rbMale = findViewById(R.id.rbGenderMale);
        txtAdddressLine1 = findViewById(R.id.txtAddresLine1);
        txtAdddressLine2 = findViewById(R.id.txtAddresLine2);
        txtAdddressLine3 = findViewById(R.id.txtAddresLine3);
        txtLandmark = findViewById(R.id.txtLandmark);
        txtPincode = findViewById(R.id.txtPInCode);
        txtCity = findViewById(R.id.txtCity);
        txtState = findViewById(R.id.txtState);



        if(m_bAddAddress){
            txtEmail.setVisibility(GONE);
//            LinearLayout layoutGender = (LinearLayout)findViewById(R.id.layoutGender);
//            layoutGender.setVisibility(GONE);

        }



    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void getFCMToken(){
        //Get Firebase FCM token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                m_fcmToken = instanceIdResult.getToken();
            }
        });
    }

    public void onDateBtn_Click(View view) {
    }

    public void onSaveOtherDetails_click(View view) {


        String userId = mAuth.getUid();

        if(m_bAddAddress == false) {
            Address objAddress = new Address();
            objAddress.AddressId = UUID.randomUUID().toString();

            User user = new User();

            String sGender = "Male";

            boolean bErrorFound = false;
            if(txtName.getText().toString().trim().equals("")){
                txtName.setError("Name can't be empty");
                bErrorFound = true;
            }



            if(txtPhone.getText().toString().trim().equals("")){
                txtPhone.setError("Phone number cannot be empty");
                bErrorFound = true;
            }

            if(txtPhone.getText().toString().trim().length() != 10){
                txtPhone.setError("Phone number has to be 10 digits number");
                bErrorFound = true;
            }

            if(!m_bAddAddress) {
                if (txtEmail.getText().toString().trim().equals("")) {
                    txtEmail.setError("Email address cannot be empty");
                    bErrorFound = true;
                }
            }

            if(txtAdddressLine1.getText().toString().trim().equals("")){
                txtAdddressLine1.setError("Address Line1 cannot be empty");
                bErrorFound = true;
            }

            if(txtAdddressLine2.getText().toString().trim().equals("")){
                txtAdddressLine2.setError("Address Line2 cannot be empty");
                bErrorFound = true;
            }

            if(txtAdddressLine3.getText().toString().trim().equals("")){
                txtAdddressLine3.setError("Address Line3 cannot be empty");
                bErrorFound = true;
            }

            if(txtPincode.getText().toString().trim().equals("")){
                txtPincode.setError("Pincode cannot be empty");
                bErrorFound = true;
            }

            if(txtCity.getText().toString().trim().equals("")){
                txtCity.setError("City cannot be empty");
                bErrorFound = true;
            }



            if(txtLandmark.getText().toString().trim().equals("")){
                txtLandmark.setError("Landmark cannot be empty");
                bErrorFound = true;
            }

            if(txtState.getText().toString().trim().equals("")){
                txtState.setError("State cannot be empty");
                bErrorFound = true;
            }

            if(bErrorFound){
                return;
            }

            user.Active = true;
            user.Name = txtName.getText().toString();
            user.Phone = txtPhone.getText().toString();
            user.Email = txtEmail.getText().toString();
            user.AddressLine1 = txtAdddressLine1.getText().toString();
            user.AddressLine2 = txtAdddressLine2.getText().toString();
            user.AddressLine3 = txtAdddressLine3.getText().toString();
            user.Pincode = txtPincode.getText().toString();
            user.buyer_area_pin = user.Pincode.substring(0, 3);
            user.City = txtCity.getText().toString();
            user.Landmark = txtLandmark.getText().toString();
            user.State = txtState.getText().toString();
            user.AddressId = objAddress.AddressId;
            user.points = 80;
            user.customer_id = userId;
            user.fcm = m_fcmToken;
            user.status = "approved";
            //   user.Active = true;

            if (rbMale.isChecked() == false) {
                sGender = "Female";
            }
            user.Gender = sGender;

            db.collection("delivery_agents").document(userId).set(user);


            objAddress.Name = user.Name;
            objAddress.AddressLine1 = user.AddressLine1;
            objAddress.AddressLine2 = user.AddressLine2;
            objAddress.AddressLine3 = user.AddressLine3;
            objAddress.City = user.City;
            objAddress.Pincode = user.Pincode;
            objAddress.buyer_area_pin = user.Pincode.substring(0, 3);
            objAddress.Phone = user.Phone;
            objAddress.State = user.State;
            objAddress.Landmark = user.Landmark;

            CommonVariables.deliveryAddress = objAddress;
            CommonVariables.loggedInUserDetails = user;

            db.collection("delivery_agents").document(userId).collection("Addresses").document(objAddress.AddressId).set(objAddress);


            LaunchLandingPage();
        }

        else
        {

            Address objAddress = new Address();
            objAddress.Name = txtName.getText().toString();
            objAddress.Phone = txtPhone.getText().toString();
            objAddress.AddressLine1 = txtAdddressLine1.getText().toString();
            objAddress.AddressLine2 = txtAdddressLine2.getText().toString();
            objAddress.AddressLine3 = txtAdddressLine3.getText().toString();
            objAddress.Pincode = txtPincode.getText().toString();
            objAddress.buyer_area_pin = objAddress.Pincode.substring(0, 3);
            objAddress.City = txtCity.getText().toString();
            objAddress.Landmark = txtLandmark.getText().toString();
            objAddress.State = txtState.getText().toString();
            objAddress.AddressId = UUID.randomUUID().toString();

            db.collection("delivery_agents").document(userId).collection("Addresses").document(objAddress.AddressId).set(objAddress);
            CommonVariables.loggedInUserDetails.AddressList.add(objAddress);
            Toast.makeText(this, "New address added", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserOtherDetails.this, LandingPage.class);
            //prevent the user to come again to this screen in he presses back btton
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);


        }

    }

    private void LaunchLandingPage(){
        Intent intent = new Intent(UserOtherDetails.this, LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    public void onLogout_Click(View view) {
        mAuth.signOut();
        CommonVariables.loggedInUserDetails = null;
        CommonVariables.deliveryAddress = null;

        Intent intent = new Intent(UserOtherDetails.this, Login.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }
}

