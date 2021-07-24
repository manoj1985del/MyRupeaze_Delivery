package com.texpediscia.myrupeazedelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.texpediscia.myrupeazedelivery.model.Address;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.texpediscia.myrupeazedelivery.model.User;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private String m_sVerificationId;
    private  String m_sPhone;
    private FirebaseAuth mAuth;
    private EditText txt1, txt2, txt3, txt4, txt5, txt6;
    private EditText txtPhone, txtEmail;
    private Button otpButton, loginButton;
    private RelativeLayout layoutPhone, layoutEmail;
    private Enums.LoginMethod loginMethod = Enums.LoginMethod.EMAIL;

    private String m_sPhoneLoginText = "Login using Phone";
    private String m_sEmailLoginText = "Login using Email";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean m_bIsPhoneLogin = false;

    //for google login
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;

    CountDownTimer aTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);


        //  List<String> lstLastMonths = CommonMethods.getInstance().getLastMonths(12);

        loginButton = findViewById(R.id.btnLogin);
        layoutPhone = findViewById(R.id.layoutPhoneLogin);
        layoutEmail = findViewById(R.id.layoutEmailLogin);

      // loginButton.setVisibility(View.GONE);

        txtEmail = findViewById(R.id.txtEmail);


        initiatePhoneLoginSettings();

        changeLayout();

    }

    private void changeLayout(){
        if(m_bIsPhoneLogin){
            loginButton.setText(m_sEmailLoginText);
            layoutEmail.setVisibility(View.GONE);
            layoutPhone.setVisibility(View.VISIBLE);
            loginMethod = Enums.LoginMethod.PHONE;
        }
        else
        {
            loginMethod = Enums.LoginMethod.EMAIL;
            loginButton.setText(m_sPhoneLoginText);
            layoutEmail.setVisibility(View.VISIBLE);
            layoutPhone.setVisibility(View.GONE);
        }

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signInWithGoogle();
                        break;
                    // ...
                }
            }
        });
    }


    private void initiatePhoneLoginSettings()
    {
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();

        txt1 = findViewById(R.id.txtOTP1);
        txt2 = findViewById(R.id.txtOTP2);
        txt3 = findViewById(R.id.txtOTP3);
        txt4 = findViewById(R.id.txtOTP4);
        txt5 = findViewById(R.id.txtOTP5);
        txt6 = findViewById(R.id.txtOTP6);

        otpButton = findViewById(R.id.btnOTP);

        txtPhone = findViewById(R.id.txtPhone);
        txtPhone.requestFocus();

        txt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if(sText.length() == 1){
                    txt2.requestFocus();
                }

            }
        });

        txt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if(sText.length() == 1){
                    txt3.requestFocus();
                }
            }
        });

        txt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if(sText.length() == 1){
                    txt4.requestFocus();
                }
            }
        });

        txt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if(sText.length() == 1){
                    txt5.requestFocus();
                }
            }
        });

        txt5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if(sText.length() == 1){
                    txt6.requestFocus();
                }
            }
        });

        txt6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sText = s.toString();
                if(sText.length() == 1){
                    String s1 = txt1.getText().toString();
                    String s2 = txt1.getText().toString();
                    String s3 = txt1.getText().toString();
                    String s4 = txt1.getText().toString();
                    String s5 = txt1.getText().toString();
                    String s6 = sText;
                    String sCode = s1 + s2 + s3 + s4 + s5 + s6;
                    verifyCode(sCode);

                }
            }
        });


//
//        txt1.addTextChangedListener(new OTPWatcher(txt1, txt2));
//        txt2.addTextChangedListener(new OTPWatcher(txt2, txt3));
//        txt3.addTextChangedListener(new OTPWatcher(txt3, txt4));
//        txt4.addTextChangedListener(new OTPWatcher(txt4, txt5));
//        txt5.addTextChangedListener(new OTPWatcher(txt5, txt6));

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                m_sVerificationId = s;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                verifyCode(code);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        };
    }

    private boolean okForOTPValidation() {
        if(txt1.getText().toString().matches("")
                || txt6.getText().toString().matches("") || txt2.getText().toString().matches("") || txt3.getText().toString().matches("")
                ||txt4.getText().toString().matches("") ||txt5.getText().toString().matches("")){
            return  false;
        }
        return true;
    }

    private  void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(m_sVerificationId, code);
        signInWithCredintial(credential);
    }

    private void signInWithCredintial(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            LaunchActivity();
                        }
                        else{
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void sendVerificationCode(String sPhone){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                sPhone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );
    }

    public void onSendOTP_Click(View view) {

        otpButton.setEnabled(false);
        otpButton.setBackgroundResource(R.drawable.blue_button_disabled);
        startTimer();
        // EditText txtPhone = findViewById(R.id.txtPhone);
        m_sPhone = txtPhone.getText().toString();
        CommonVariables.Phone = m_sPhone;
        String sPhone = "+91"+m_sPhone;
        sendVerificationCode( sPhone);
        txt1.requestFocus();
        Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show();
    }

    public void onLoginbtn_Click(View view) {
        m_bIsPhoneLogin = !m_bIsPhoneLogin;
        changeLayout();

    }

    private void startTimer(){
        aTimer =  new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                otpButton.setText("Time Remaining -> " + millisUntilFinished / 1000);
                //  mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                otpButton.setEnabled(true);
                otpButton.setBackgroundResource(R.drawable.blue_button);
                otpButton.setText("SEND OTP");
            }
        }.start();
    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onRegister_Click(View view) {
        Intent intent = new Intent(Login.this, RegisterUser.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void onLoginEmail_Click(View view) {
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPwd = findViewById(R.id.txtPassword);

        signInWithEmail(txtEmail.getText().toString(), txtPwd.getText().toString());

    }


    private void signInWithEmail(String email, String password){

        CommonVariables.Email = email;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithEmail:success");

                            LaunchActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            //   Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed. Reason: " + task.getException().getMessage() ,
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    public void onGoogleSignin_Click(View view) {

        loginMethod = Enums.LoginMethod.GOOGLE;
//        if(mGoogleSignInClient == null) {
//            // Configure sign-in to request the user's ID, email address, and basic
//            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                    .requestIdToken(getString(R.string.default_web_client_id))
//                    .requestEmail()
//                    .build();
//
//            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        }

        signInWithGoogle();

    }

    private void signInWithGoogle() {

        if(mGoogleSignInClient == null) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }


        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    //@Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(Login.this, "Google sign in failed: Reason - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        CommonVariables.Email = acct.getEmail();
        CommonVariables.UserName = acct.getDisplayName();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            LaunchActivity();
                            //  updateUI(user);
                        } else {
                            Exception ex = task.getException();

                            // If sign in fails, display a message to the user.
                            Log.w("google exception", "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }

                        // ...
                    }
                });
    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            FirebaseGoogleAuth(account);
//            CommonVariables.Email = account.getEmail();
//            CommonVariables.UserName = account.getDisplayName();
//
//            LaunchActivity();
//            // Signed in successfully, show authenticated UI.
//            //updateUI(account);
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
//            // updateUI(null);
//        }
//    }

//    private void FirebaseGoogleAuth(GoogleSignInAccount account){
//        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                Log.w("TAG", "signInResult:failed code=");
//            }
//        });
//    }



    public void onForgotPwd_Click(View view) {


        EditText txtEmail = findViewById(R.id.txtEmail);
        String sEmail = txtEmail.getText().toString();
        FirebaseAuth.getInstance().sendPasswordResetEmail(sEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password recovery mail sent", Toast.LENGTH_SHORT).show();
                            //  Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    private void LaunchLandingPage(){
        Intent intent = new Intent(Login.this, LandingPage.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void LaunchUserRegistratoinActivity(){
        Intent intent = new Intent(Login.this, RegisterUser.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void LaunchOtherDetailsActivity(){
        Intent intent = new Intent(Login.this, UserOtherDetails.class);
        //prevent the user to come again to this screen in he presses back btton
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void LaunchActivity(){
        CommonVariables.m_sFirebaseUserId = mAuth.getUid();
        //check if this firebase user exists in users table. if not ask for some additional detials to enter
        DocumentReference docRef = db.collection("delivery_agents").document(CommonVariables.m_sFirebaseUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        CommonVariables.loggedInUserDetails = document.toObject(User.class);
                        setAddress();


                    } else {
                        LaunchOtherDetailsActivity();
                    }
                }
                else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void LoadAddresses(){
        db.collection("delivery_agents").document(CommonVariables.m_sFirebaseUserId).collection("Addresses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Address objAddress = document.toObject(Address.class);
                                CommonVariables.loggedInUserDetails.AddressList.add(objAddress);
                            }
                            LaunchLandingPage();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void setAddress(){
        DocumentReference docRef = db.collection("delivery_agents").document(CommonVariables.m_sFirebaseUserId).collection("Addresses")
                .document(CommonVariables.loggedInUserDetails.AddressId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CommonVariables.deliveryAddress = document.toObject(Address.class);
                        LoadAddresses();
                    }
                }
                else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }


}
