package dev.sma.uos.manifest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import dev.sma.uos.Common;
import dev.sma.uos.R;
import dev.sma.uos.Utils;
import dev.sma.uos.activities.MainActivity;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    ///////////////// define var ///////////////
    private EditText edit_password, phone;
    private TextView txt_forgetpassword, txt_signup, txt_close;
    private AppCompatButton btn_login;
    boolean visibility;
    private FirebaseFirestore firestore;
    private CountryCodePicker mCountryCodePicker;
    private String completenumber;
    private Utils utils;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /////////////// ini var ////////////////////
        edit_password = findViewById(R.id.edit_password);
        phone = findViewById(R.id.phone);
        txt_forgetpassword = findViewById(R.id.txt_forgetpassword);
        txt_signup = findViewById(R.id.txt_signup);
        imgBack = findViewById(R.id.imgBack);
        btn_login = findViewById(R.id.btn_login);
        mCountryCodePicker = findViewById(R.id.ccp);

        /////////////////////// ini db ////////////////////
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(this);

        /////////////////// implement listener ///////////////
        txt_forgetpassword.setOnClickListener(this);
        txt_signup.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_forgetpassword:
                startActivity(new Intent(getApplicationContext(), ActivityForgetPasswordAuth.class));
                finish();
                break;
            case R.id.txt_signup:
                if (FirebaseAuth.getInstance().getUid() != null) {
                    startActivity(new Intent(getApplicationContext(), SignUp_Activity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginWithOTP.class));
                    finish();
                }

                break;
            case R.id.btn_login:
                if (checkEmpty()) {

                    ///////////// get complete number with country code ////////////
                    mCountryCodePicker.registerCarrierNumberEditText(phone);
                    completenumber = mCountryCodePicker.getFullNumberWithPlus().replace("", "").trim();
                    // Toast.makeText(this, ""+completenumber, Toast.LENGTH_SHORT).show();

                    auth(completenumber, edit_password.getText().toString().trim());

                }
                break;
            case R.id.imgBack:
                finishAffinity();
                break;
        }
    }

    private void auth(String number, String password) {


        utils.startLoading();
        firestore.collection(Common.PATIENT).whereEqualTo(Common.PATIENT_NUMBER, number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getString(Common.PASSWORD).equals(password)) {
                                        utils.putToken(document.getId());

                                        Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                                        intent.putExtra("documentID", document.getId());
                                        startActivity(intent);
                                        finish();

                                        utils.endLoading();
                                    } else
                                        utils.endLoading();
                                    edit_password.setError("Please Enter Correct Password");

                                }
                            } else
                                utils.endLoading();
                            phone.setError("Please Enter Correct Number");
                        }
                    }
                });
    }

    private boolean checkEmpty() {
        Boolean isEmpty = false;
        if (phone.getText().toString().trim().isEmpty())
            phone.setError("Please Enter Email");
        else if (edit_password.getText().toString().trim().isEmpty())
            edit_password.setError("Please Enter Password");
        else isEmpty = true;
        return isEmpty;
    }
}