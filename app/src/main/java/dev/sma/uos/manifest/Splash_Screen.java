package dev.sma.uos.manifest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import dev.sma.uos.Common;
import dev.sma.uos.R;
import dev.sma.uos.Utils;
import dev.sma.uos.activities.MainActivity;

public class Splash_Screen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    FirebaseAuth mAuth;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                utils = new Utils(Splash_Screen.this);

                if (utils.isLoggedIn()) {

                    FirebaseFirestore.getInstance().collection(Common.PATIENT)
                            .document(utils.getToken())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful() && task.getResult().exists()) {

                                        DocumentSnapshot documentSnapshot = task.getResult();

                                        if (documentSnapshot.getString(Common.PATIENT_NAME).equals("")
                                                || documentSnapshot.getString(Common.PATIENT_PIC).equals("")
                                                || documentSnapshot.getString(Common.PATIENT_EMAIL).equals("")
                                                || documentSnapshot.getString(Common.PATIENT_AGE).equals("")
                                                || documentSnapshot.getString(Common.PASSWORD).equals("")) {

                                            startActivity(new Intent(Splash_Screen.this, SignUp_Activity.class));
                                            finish();

                                        } else {
                                            startActivity(new Intent(Splash_Screen.this, MainActivity.class));
                                            finish();
                                        }

                                    }

                                }
                            });

                    /////////////////////
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }

                                    // Get new FCM registration token
                                    String token = task.getResult();

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("patientFCMToken", task.getResult());
                                    FirebaseFirestore.getInstance().collection(Common.PATIENT)
                                            .document(utils.getToken())
                                            .update(map);

                                }
                            });

                } else {

                    startActivity(new Intent(Splash_Screen.this, ActivityLogin.class));
                    finish();

                }
            }
        }, SPLASH_TIME_OUT);


    }
}