package dev.sma.uos.manifest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import dev.sma.uos.R;

public class LoginWithOTP extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    public static AlertDialog alertDialog;
    Context context;
    private CountryCodePicker country_code;
    private EditText input_phone;
    private TextView title, send_otp;
    private String completenumber;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_otp);

        context = LoginWithOTP.this;

        country_code = findViewById(R.id.country_code);
        input_phone = findViewById(R.id.input_phone);
        send_otp = findViewById(R.id.send_otp);
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUp_Activity.class));
                finish();
            }
        });

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (input_phone == null && input_phone.equals("")) {
                    Toast.makeText(context, "Please Enter Number", Toast.LENGTH_SHORT).show();

                } else if (input_phone.length() < 8) {

                    Toast.makeText(context, "Please Complete Number: Hint 333 3333333", Toast.LENGTH_LONG).show();

                } else {

                    Log.d("TAG", input_phone.getText().toString());


                    ///////////// get complete number with country code ////////////
                    country_code.registerCarrierNumberEditText(input_phone);
                    completenumber = country_code.getFullNumberWithPlus().replace("", "").trim();
                    // Toast.makeText(this, ""+completenumber, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, OtpVerify.class);
                    intent.putExtra("number", completenumber);
                    startActivity(intent);
                    finish();


                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),SignUp_Activity.class));
    }
}