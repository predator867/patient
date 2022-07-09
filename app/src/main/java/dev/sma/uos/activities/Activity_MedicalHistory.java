package dev.sma.uos.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dev.sma.uos.Common;
import dev.sma.uos.R;
import dev.sma.uos.Utils;

public class Activity_MedicalHistory extends AppCompatActivity {

    private ImageView documentPhoto;

    TextView edt_disease, edt_height, edt_weight, edt_BType, edt_skin, edt_throat, edt_indigestion,
            edt_teeth, edt_ears, edt_lungs, edt_vision, edt_heart, edt_Abdomen, edt_urine, edt_BPressure;

    ProgressDialog progressDialog;

    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        edt_disease = findViewById(R.id.edt_disease);
        edt_height = findViewById(R.id.edt_height);
        edt_weight = findViewById(R.id.edt_weight);
        edt_BType = findViewById(R.id.edt_BType);
        edt_skin = findViewById(R.id.edt_skin);
        edt_throat = findViewById(R.id.edt_throat);
        edt_indigestion = findViewById(R.id.edt_indigestion);
        edt_teeth = findViewById(R.id.edt_teeth);
        edt_ears = findViewById(R.id.edt_ears);
        edt_lungs = findViewById(R.id.edt_lungs);
        edt_vision = findViewById(R.id.edt_vision);
        edt_heart = findViewById(R.id.edt_heart);
        edt_Abdomen = findViewById(R.id.edt_Abdomen);
        edt_urine = findViewById(R.id.edt_urine);
        edt_BPressure = findViewById(R.id.edt_BPressure);
        documentPhoto = findViewById(R.id.documentPhoto);

        utils = new Utils(this);

        progressDialog = new ProgressDialog(Activity_MedicalHistory.this);
        progressDialog.setMessage("Loading...");


        FirebaseFirestore.getInstance()
                .collection(Common.PATIENT)
                .document(utils.getToken())
                .collection("LAB")
                .document(utils.getToken())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {

                            DocumentSnapshot document = task.getResult();

                            edt_disease.setText(document.getString("edt_disease"));
                            edt_height.setText(document.getString("edt_height"));
                            edt_weight.setText(document.getString("edt_weight"));
                            edt_BType.setText(document.getString("edt_BType"));
                            edt_skin.setText(document.getString("edt_skin"));
                            edt_throat.setText(document.getString("edt_throat"));
                            edt_indigestion.setText(document.getString("edt_indigestion"));
                            edt_ears.setText(document.getString("edt_ears"));
                            edt_vision.setText(document.getString("edt_vision"));
                            edt_Abdomen.setText(document.getString("edt_Abdomen"));
                            edt_urine.setText(document.getString("edt_urine"));
                            edt_BPressure.setText(document.getString("edt_BPressure"));
                            edt_teeth.setText(document.getString("edt_teeth"));
                            edt_lungs.setText(document.getString("edt_lungs"));
                            edt_heart.setText(document.getString("edt_heart"));

                            String profilePic = document.getString("labDocumentPic");

                            if (!profilePic.isEmpty()) {
                                Glide.with(getApplicationContext()).load(profilePic).into(documentPhoto);
                            } else {
                                documentPhoto.setImageResource(R.drawable.user_profile);
                            }


                            progressDialog.dismiss();

                        }
                    }
                });

    }
}