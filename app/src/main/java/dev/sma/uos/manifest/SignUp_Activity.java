package dev.sma.uos.manifest;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.sma.uos.Common;
import dev.sma.uos.R;

public class SignUp_Activity extends AppCompatActivity {

    CircleImageView user_photo;
    RelativeLayout change_photo;
    TextInputLayout input_layout_name, input_layout_email, input_layout_experience, input_layout_address;
    EditText name, email, experience, address;
    TextView txt_dob, txt_addDOB, txt_login;
    AppCompatButton btn_signUp;
    private EditText edit_password, edit_passwordconfirm;

    FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static ProgressBar progressBar_subs;
    ByteArrayOutputStream stream;
    Bitmap bitmap;
    boolean imageUploadChecker = false;
    private String dob = "select DOB";
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        user_photo = findViewById(R.id.user_photo);
        change_photo = findViewById(R.id.change_photo);
        input_layout_name = findViewById(R.id.input_layout_name);
        input_layout_email = findViewById(R.id.input_layout_email);
        input_layout_address = findViewById(R.id.input_layout_address);
        input_layout_experience = findViewById(R.id.input_layout_experience);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        experience = findViewById(R.id.experience);
        txt_dob = findViewById(R.id.txt_dob);
        txt_addDOB = findViewById(R.id.txt_addDOB);
        txt_login = findViewById(R.id.txt_login);
        btn_signUp = findViewById(R.id.btn_signUp);
        edit_password = findViewById(R.id.edit_password);
        edit_passwordconfirm = findViewById(R.id.edit_passwordconfirm);
        imgBack = findViewById(R.id.imgBack);

        ////////////// ini db /////////////
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginWithOTP.class));
                finish();
            }
        });

        txt_addDOB.setOnClickListener(view -> {

            openDatePickerDialog(view);

        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ActivityLogin.class));
                finish();
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (filePath == null) {
                    Toast.makeText(SignUp_Activity.this, "Please select image", Toast.LENGTH_SHORT).show();
                } else if (name.getText().toString().equals("")) {
                    name.setError("Select Name");
                } else if (email.getText().toString().equals("")) {
                    email.setError("Select Email");
                } else if (address.getText().toString().equals("")) {
                    address.setError("Enter Address");
                } else if (edit_passwordconfirm.getText().toString().trim().isEmpty())
                    edit_passwordconfirm.setError("Please Enter Password");
                else if (edit_password.getText().toString().trim().isEmpty())
                    edit_password.setError("Please Enter Password");
                else if (edit_password.getText().toString().length() < 6) {
                    edit_password.setError("Please Enter 6 characters Password");
                } else if (edit_passwordconfirm.getText().toString().length() < 6) {
                    edit_passwordconfirm.setError("Please Enter 6 characters Password");
                } else if (!edit_password.getText().toString().equals(edit_passwordconfirm.getText().toString())) {
                    edit_passwordconfirm.setError("Passwords Don't Match");
                } else if (dob.equals("select DOB")) {
                    Toast.makeText(SignUp_Activity.this, "Enter DOB", Toast.LENGTH_SHORT).show();
                } else {

                    /////////////// show progress dialog /////////////
                    final ProgressDialog progressDialog = new ProgressDialog(SignUp_Activity.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    ////////////////// save images in firebase storage /////////////////////
                    DocumentReference documentReference1;
                    documentReference1 = firestore.collection(Common.PATIENT).document(mAuth.getUid());


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(Common.PATIENT_PIC)
                            .child(documentReference1.getId());


                    BitmapDrawable drawable = (BitmapDrawable) user_photo.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality

                    UploadTask uploadTask = storageReference.putBytes(stream.toByteArray());

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTaskSubcategory = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTaskSubcategory.isSuccessful()) ;
                            Uri downladUri1 = uriTaskSubcategory.getResult();
                            if (uriTaskSubcategory.isSuccessful()) {

                                Log.d("TAG", "onSuccess: check");
                                Map<String, Object> m = new HashMap<>();
                                m.put(Common.PATIENT_PIC, downladUri1.toString());
                                m.put(Common.PATIENT_NAME, name.getText().toString());
                                m.put(Common.PATIENT_EMAIL, email.getText().toString());
                                m.put(Common.PATIENT_ADDRESS, address.getText().toString());
                                m.put(Common.PASSWORD, edit_password.getText().toString());
                                m.put(Common.PATIENT_AGE, dob);
                                m.put("memberShip", "0");
                                m.put("cc", "");

                                firestore.collection(Common.PATIENT)
                                        .document(mAuth.getUid())
                                        .update(m)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();

                                                startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", e.getMessage());
                                                Toast.makeText(SignUp_Activity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
//                                                Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploading Data " + (int) progress + "%");
                                }
                            });


                }
            }
        });

    }

    private void openDatePickerDialog(View view) {

        Calendar cal = Calendar.getInstance();
        // Get Current Date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (v, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    txt_dob.setText(selectedDate);
                    dob = selectedDate;
                   /* switch (v.getId()) {
                        case R.id.txt_addDOB:
                            ((TextView) v).setText(selectedDate);

                            Log.e("DOB selected", dob);
                            break;
                    }*/
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.show();

    }


    /////////////////////////  IMAGE  //////////////////
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                user_photo.setImageBitmap(bitmap);
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality
                imageUploadChecker = true;


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),ActivityLogin.class));

    }

}