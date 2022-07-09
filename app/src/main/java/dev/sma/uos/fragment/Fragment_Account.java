package dev.sma.uos.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import dev.sma.uos.Common;
import dev.sma.uos.R;
import dev.sma.uos.Utils;
import dev.sma.uos.activities.Activity_MedicalHistory;
import dev.sma.uos.activities.MainActivity;
import dev.sma.uos.manifest.ActivityLogin;


public class Fragment_Account extends Fragment implements View.OnClickListener {

    //////////// ini var //////////////////
    private TextView txtname, txt_number, txt_email, txt_age, txt_experience, txt_address, txt_cc;
    private FirebaseFirestore firestore;
    private ImageView img_logout, img_edit;
    private LinearLayout layage, layexperience, laycc, layaddress;
    private final int PICK_IMAGE_REQUEST = 71;
    private static int NOTIFY_ID = 786;
    private Uri filePath;
    Utils utils;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    ByteArrayOutputStream stream;
    Bitmap bitmap;
    boolean imageUploadChecker = false;

    Context context;
    Activity activity;

    RelativeLayout lay_medicalHistory;

    ImageView img_generate, imaQrCode;
    private Bitmap bitmap_img;


    CircleImageView user_photo;
    RelativeLayout change_photo;
    private String profilePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__account, container, false);

        ///////////// ini var ////////////////////
        txt_cc = view.findViewById(R.id.txt_cc);
        laycc = view.findViewById(R.id.laycc);
        img_generate = view.findViewById(R.id.img_generate);
        imaQrCode = view.findViewById(R.id.imaQrCode);
        txtname = view.findViewById(R.id.txtname);
        txt_number = view.findViewById(R.id.txt_number);
        txt_email = view.findViewById(R.id.txt_email);
        txt_age = view.findViewById(R.id.txt_age);
        txt_experience = view.findViewById(R.id.txt_experience);
        txt_address = view.findViewById(R.id.txt_address);
        img_logout = view.findViewById(R.id.img_logout);
        img_edit = view.findViewById(R.id.img_edit);
        user_photo = view.findViewById(R.id.user_photo);
        change_photo = view.findViewById(R.id.change_photo);
        layage = view.findViewById(R.id.layage);
        layexperience = view.findViewById(R.id.layexperience);
        layaddress = view.findViewById(R.id.layaddress);
        lay_medicalHistory = view.findViewById(R.id.lay_medicalHistory);


        //////////////// ini db ///////////////////
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        utils = new Utils(getContext());

        ///////// get context
        context = getContext();
        activity = (Activity) view.getContext();


        //////////////// implement clcik listener /////////////
        layaddress.setOnClickListener(this);
        layage.setOnClickListener(this);
        layexperience.setOnClickListener(this);
        user_photo.setOnClickListener(this);
        change_photo.setOnClickListener(this);
        img_edit.setOnClickListener(this);
        img_logout.setOnClickListener(this);
        lay_medicalHistory.setOnClickListener(this);
        img_generate.setOnClickListener(this);
        imaQrCode.setOnClickListener(this);

        //////////////// get profile data ///////////////
        getprofile();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_logout:
                logout();
                break;
            case R.id.img_edit:
                editemail();
                break;
            case R.id.user_photo:

                Dialog dialog2 = new Dialog(context);
                dialog2.setContentView(R.layout.dialog_photobig);
                ImageView temp_photo = dialog2.findViewById(R.id.img_photo);

                if (!profilePic.isEmpty()) {
                    Glide.with(context).load(profilePic).into(temp_photo);
                    dialog2.show();
                } else {
                    user_photo.setImageResource(R.drawable.user_profile);
                }

                break;
            case R.id.change_photo:
                imgProfile();
                break;
            case R.id.layage:

                break;
            case R.id.layexperience:

                break;
            case R.id.layaddress:
                addAddress();
                break;
            case R.id.lay_medicalHistory:
                startActivity(new Intent(context, Activity_MedicalHistory.class));
                break;
            case R.id.img_generate:

                if (utils.getToken() != null) {

                    String input = utils.getToken();

                    // ini multi former text

                    MultiFormatWriter writer = new MultiFormatWriter();

                    try {
                        BitMatrix matrix = writer.encode(input, BarcodeFormat.QR_CODE
                                , 350, 350);

                        // ini Barcode
                        BarcodeEncoder encoder = new BarcodeEncoder();

                        // ini bitmap
                        bitmap_img = encoder.createBitmap(matrix);

                        imaQrCode.setImageBitmap(bitmap_img);

                        // ini input manager

//                        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(
//                                Context.INPUT_METHOD_SERVICE
//                        );
//
//                        // hide keyboard
//                        manager.hideSoftInputFromWindow(edt_enterCode.getApplicationWindowToken(), 0);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }


                }
                break;
            case R.id.imaQrCode:

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_photo);
                ImageView temp_pic = dialog.findViewById(R.id.img_photo);

                if (bitmap_img != null) {

                    temp_pic.setImageBitmap(bitmap_img);

                    dialog.show();

                }


                break;
        }
    }

    private void getprofile() {

        utils.startLoading();

        firestore.collection(Common.PATIENT)
                .document(utils.getToken())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {

                            DocumentSnapshot document = task.getResult();

                            String name = document.getString(Common.PATIENT_NAME);
                            String age = document.getString(Common.PATIENT_AGE);
                            String number = document.getString(Common.PATIENT_NUMBER);
                            String email = document.getString(Common.PATIENT_EMAIL);
                            String address = document.getString(Common.PATIENT_ADDRESS);
                            String cc = document.getString("cc");
                            String cc_date = document.getString("cc_date");
                            profilePic = document.getString(Common.PATIENT_PIC);


                            txtname.setText(name);
                            txt_number.setText(number);
                            txt_email.setText(email);
                            txt_age.setText(age);
                            txt_address.setText(address);

                            if (!profilePic.isEmpty()) {
                                Glide.with(context).load(profilePic).into(user_photo);
                            } else {
                                user_photo.setImageResource(R.drawable.user_profile);
                            }


                            if (!cc.isEmpty()) {
                                laycc.setVisibility(View.VISIBLE);
                                txt_cc.setText(cc);
                            } else {

                            }

                            if (!cc_date.isEmpty()) {

                                // timestamp
                                String str_date = cc_date;
                                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = null;
                                long timestampForExpDate = 0;
                                try {
                                    date = (Date) formatter.parse(str_date);

                                    long output = date.getTime() / 1000L;
                                    String str = Long.toString(output);
                                    timestampForExpDate = Long.parseLong(str) * 1000;

                                    Log.d("CHECK", " TIME onCreate: " + timestampForExpDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Log.d("CHECK", cc_date + " onComplete: " + timestampForExpDate);
                                Log.d("CHECK", getTSForCurrentDate() + " onComplete: " + timestampForExpDate);

                                if (getTSForCurrentDate() >= timestampForExpDate) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                        NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
                                        NotificationManager manager = context.getSystemService(NotificationManager.class);
                                        manager.createNotificationChannel(channel);

                                        ///////////// without firebase notification
                                        ////////// create bulder
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My Notification");

                                        /////////// set title, message,icon
                                        builder.setContentTitle("Coupon Code Expire");
                                        builder.setContentText("Dear Patient, your coupon code has been expire");
                                        builder.setSmallIcon(R.drawable.logo);

                                        //// create pending intent to lunch target activity when user click on notification
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.putExtra("keyID", NOTIFY_ID);

                                        PendingIntent pendingIntent = PendingIntent.getActivity(context
                                                , 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                        ///////// set intent to builder
                                        builder.setContentIntent(pendingIntent);

                                        ////// set action
                                        NotificationCompat.Action.Builder action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Read", pendingIntent);

                                        builder.addAction(action.build());


                                        Notification notification = builder.build();

                                        ////////pass notification to notification manager
                                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                                        managerCompat.notify(NOTIFY_ID, notification);

                                        ///////// plus ++ for create more notification

                                        NOTIFY_ID++;


                                    }

                                }

                            }

                            utils.endLoading();

                        }
                    }
                });
    }

    private Long getTSForCurrentDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date cd = new Date();

        String str_date = dateFormat.format(cd);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        long tsForCurrentDate = 0;
        try {
            date = (Date) formatter.parse(str_date);

            long output = date.getTime() / 1000L;
            String str = Long.toString(output);
            tsForCurrentDate = Long.parseLong(str) * 1000;

            Log.d("TAG", " TIME onCreate: " + tsForCurrentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return tsForCurrentDate;
    }


    private void addAddress() {

        Dialog dialog = new Dialog(this.getActivity());
        dialog.setContentView(R.layout.dialog_editname);
        AppCompatButton btn_update = dialog.findViewById(R.id.btn_update);
        EditText edit_update = dialog.findViewById(R.id.edit_update);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView txt_title = dialog.findViewById(R.id.txt_title);

        edit_update.setHint("Address");
        txt_title.setText("Update your Address");

        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_update.getText().toString().trim().isEmpty()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put(Common.PATIENT_ADDRESS, edit_update.getText().toString().trim());
                    firestore.collection(Common.PATIENT).document(utils.getToken())
                            .update(map).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(ActivityResetPassword.this, ""+txt_newPassword, Toast.LENGTH_SHORT).show();

                                    getprofile();

                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getContext(), "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

                                }

                            });

                } else {
                    edit_update.setError("Field can't be Empty");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    private void editemail() {

        Dialog dialog = new Dialog(this.getActivity());
        dialog.setContentView(R.layout.dialog_editname);
        AppCompatButton btn_update = dialog.findViewById(R.id.btn_update);
        EditText edit_update = dialog.findViewById(R.id.edit_update);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView txt_title = dialog.findViewById(R.id.txt_title);

        edit_update.setHint("Email");
        txt_title.setText("Update your Email");

        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_update.getText().toString().trim().isEmpty()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put(Common.PATIENT_EMAIL, edit_update.getText().toString().trim());
                    firestore.collection(Common.PATIENT).document(utils.getToken())
                            .update(map).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(ActivityResetPassword.this, ""+txt_newPassword, Toast.LENGTH_SHORT).show();

                                    getprofile();

                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getContext(), "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

                                }

                            });

                } else {
                    edit_update.setError("Field can't be Empty");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void logout() {

        MaterialDialog mDialog = new MaterialDialog.Builder(this.getActivity())
                .setTitle("Logout")
                .setMessage("Are you sure want to logout!")
                .setCancelable(false)
                .setPositiveButton("Logout", R.drawable.ic_baseline_logout_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        utils.logout();
                        startActivity(new Intent(getContext(), ActivityLogin.class));
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_baseline_cancel_presentation_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();

    }

    private void imgProfile() {
        MaterialDialog mDialog = new MaterialDialog.Builder(this.getActivity())
                .setTitle("Upload")
                .setMessage("Are you sure want to Upload Image!")
                .setCancelable(false)
                .setPositiveButton("Upload", R.drawable.ic_baseline_drive_folder_upload_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {


                        chooseImage();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_baseline_cancel_presentation_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
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
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                user_photo.setImageBitmap(bitmap);
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality
                imageUploadChecker = true;

                //////////// saveimg in user profile and storage
                updateimg();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateimg() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if (filePath != null) {

            DocumentReference documentReference1;
            documentReference1 = firestore.collection(Common.PATIENT).document(utils.getToken());


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

                                Map<String, Object> objectMap = new HashMap<>();
                                objectMap.put(Common.PATIENT_PIC, downladUri1.toString());

                                firestore.collection(Common.PATIENT)
                                        .document(utils.getToken())
                                        .update(objectMap);

                                getprofile();

                                progressDialog.dismiss();

//                        progressBar.setVisibility(android.view.View.GONE);
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


}