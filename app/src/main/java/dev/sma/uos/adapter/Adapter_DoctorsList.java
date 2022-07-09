package dev.sma.uos.adapter;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dev.sma.uos.Common;
import dev.sma.uos.Fcm.FCM_Notification;
import dev.sma.uos.R;
import dev.sma.uos.Utils;
import dev.sma.uos.activities.MainActivity;
import dev.sma.uos.model.Model_DoctorsList;

public class Adapter_DoctorsList extends FirestoreRecyclerAdapter<Model_DoctorsList, Adapter_DoctorsList.ViewHolder> {

    Context context;
    FirebaseFirestore firestore;
    Utils utils;
    String name, number, email, profilePic, class_tutor, age, address, memberShip;
    private String patientFCMToken;
    private int counter = 0;
    private static int NOTIFY_ID = 786;


    public Adapter_DoctorsList(@NonNull FirestoreRecyclerOptions<Model_DoctorsList> options, Context context) {
        super(options);
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(context);
        //////////////// get profile data ///////////////
        getprofile();
    }

    private void getprofile() {

        firestore.collection(Common.PATIENT)
                .document(utils.getToken())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {

                            DocumentSnapshot document = task.getResult();

                            name = document.getString(Common.PATIENT_NAME);
                            age = document.getString(Common.PATIENT_AGE);
                            number = document.getString(Common.PATIENT_NUMBER);
                            email = document.getString(Common.PATIENT_EMAIL);
                            address = document.getString(Common.PATIENT_ADDRESS);
                            profilePic = document.getString(Common.PATIENT_PIC);
                            patientFCMToken = document.getString("patientFCMToken");
                            memberShip = document.getString("memberShip");


                        }
                    }
                });
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model_DoctorsList model) {


        holder.txt_address.setText("Address : " + model.getAddress());
        holder.txt_experienec.setText("Experience : " + model.getExperience() + " Year");

        holder.txtname.setText(model.getName());
        holder.txt_number.setText(model.getNumber());
        holder.txt_email.setText(model.getEmail());

        Glide.with(holder.itemView)
                .load(model.getDoctor_pic())
                .fitCenter().into(holder.imageprofile);

        holder.img_moreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.img_moreinfo.setVisibility(View.GONE);
                holder.img_hideinfo.setVisibility(View.VISIBLE);

                holder.lay_address.setVisibility(View.VISIBLE);
                holder.lay_experience.setVisibility(View.VISIBLE);

                holder.lay_callChat.setVisibility(View.VISIBLE);
                holder.lay_pay.setVisibility(View.VISIBLE);

            }
        });

        holder.img_hideinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.img_moreinfo.setVisibility(View.VISIBLE);
                holder.img_hideinfo.setVisibility(View.GONE);

                holder.lay_address.setVisibility(View.GONE);
                holder.lay_experience.setVisibility(View.GONE);

                holder.lay_callChat.setVisibility(View.GONE);
                holder.lay_pay.setVisibility(View.GONE);


            }
        });

        holder.lay_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_another_acount);
                Button btn_use = dialog.findViewById(R.id.btn_use);
                TextView txt_easy = dialog.findViewById(R.id.txt_easy);
                TextView txt_jazz = dialog.findViewById(R.id.txt_jazz);
                TextView txt_bank = dialog.findViewById(R.id.txt_bank);

                dialog.show();

                txt_easy.setText("EidiPaisa " + model.getEasy());
                txt_jazz.setText("Jazz Cash " + model.getJazz());
                txt_bank.setText("Bank " + model.getBank());

                btn_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }
        });

        holder.laycontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contact = "+92 340 9009191"; // use country code with your phone number
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + model.getNumber()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

        holder.laychat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contact = model.getNumber(); // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = context.getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    // Toast.makeText(MainActivity.activity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });


        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection(Common.DOCTOR)
                        .document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId())
                        .collection(Common.APPOINTMENT)
                        .document();

                Map<String, Object> map = new HashMap<>();

                map.put(Common.REQUEST_STATUS, "pending");
                map.put(Common.PATIENT_ID, utils.getToken());
                map.put(Common.DOCTOR_ID, getSnapshots().getSnapshot(holder.getAdapterPosition()).getId());
                map.put(Common.PATIENT_NAME, name);
                map.put(Common.PATIENT_AGE, age);
                map.put(Common.PATIENT_NUMBER, number);
                map.put(Common.PATIENT_EMAIL, email);
                map.put(Common.PATIENT_ADDRESS, address);
                map.put(Common.PATIENT_PIC, profilePic);
                map.put("patientFCMToken", patientFCMToken);
                map.put("date", getdate());


                documentReference.set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {


                                try {
                                    new FCM_Notification(
                                            model.getDoctorFCMToken(),
                                            "New Booking",
                                            name + " has booked you for online consultation",
                                            context
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                FirebaseFirestore.getInstance()
                                        .collection(Common.PATIENT)
                                        .document(utils.getToken())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful() && task.getResult().exists()) {

                                                    DocumentSnapshot document = task.getResult();

                                                    counter = Integer.parseInt(document.getString("memberShip"));

                                                    FirebaseFirestore.getInstance()
                                                            .collection(Common.DOCTOR)
                                                            .document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId())
                                                            .collection(Common.APPOINTMENT)
                                                            .whereEqualTo(Common.PATIENT_ID, utils.getToken())
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                                        for (QueryDocumentSnapshot query : task.getResult()) {
                                                                            if (query.getString(Common.REQUEST_STATUS).equals("pending")
                                                                                    || query.getString(Common.REQUEST_STATUS).equals("pending")) {


                                                                                String date = query.getString("date");
                                                                                String month = date.substring(2, 5);

                                                                                String current_date = getdate();
                                                                                String current_month = current_date.substring(2, 5);

                                                                                Log.d("DATEE", month + " onComplete: " + current_month);
                                                                                if (month.equals(current_month)) {
                                                                                    counter++;

                                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                                    hashMap.put("memberShip", counter + "");

                                                                                    FirebaseFirestore.getInstance()
                                                                                            .collection(Common.PATIENT)
                                                                                            .document(utils.getToken())
                                                                                            .update(hashMap)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {

                                                                                                    FirebaseFirestore.getInstance()
                                                                                                            .collection(Common.PATIENT)
                                                                                                            .document(utils.getToken())
                                                                                                            .get()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                    if (task.isSuccessful() && task.getResult().exists()) {

                                                                                                                        DocumentSnapshot document = task.getResult();

                                                                                                                        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

                                                                                                                        String membership = document.getString("memberShip");
                                                                                                                        int ms = Integer.parseInt(membership);

                                                                                                                        if (ms >= 3) {

                                                                                                                            int max = 100000000;
                                                                                                                            int random = (int) (Math.random() * max);
                                                                                                                            StringBuffer sb = new StringBuffer();
                                                                                                                            while (random > 0) {
                                                                                                                                sb.append(chars[random % chars.length]);
                                                                                                                                random /= chars.length;
                                                                                                                            }
                                                                                                                            String couponCode = sb.toString();

                                                                                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                                                                                            hashMap.put("cc", couponCode);
                                                                                                                            hashMap.put("cc_date", getExpDate());

                                                                                                                            FirebaseFirestore.getInstance()
                                                                                                                                    .collection(Common.PATIENT)
                                                                                                                                    .document(utils.getToken())
                                                                                                                                    .update(hashMap)
                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onSuccess(Void unused) {


                                                                                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                                                                                                                                NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
                                                                                                                                                NotificationManager manager = context.getSystemService(NotificationManager.class);
                                                                                                                                                manager.createNotificationChannel(channel);

                                                                                                                                                ///////////// without firebase notification
                                                                                                                                                ////////// create bulder
                                                                                                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My Notification");

                                                                                                                                                /////////// set title, message,icon
                                                                                                                                                builder.setContentTitle("Discount Offer");
                                                                                                                                                builder.setContentText("Congratulation! You got discount offer 50% off on your next appointment");
                                                                                                                                                builder.setSmallIcon(R.drawable.logo);

                                                                                                                                                ////////// notification property
                                                                                                                                                builder.setLights(Color.BLUE, 200, 200);

                                                                                                                                                Uri sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                                                                                                                builder.setSound(sounduri);

                                                                                                                                                long vibrate[] = {100, 500, 100, 500};
                                                                                                                                                builder.setVibrate(vibrate);

                                                                                                                                                builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

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

//                                                                                                                            //////// set big textstyle
//                                                                                                                            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
//                                                                                                                            bigTextStyle.setBigContentTitle("Discount Offer");
//                                                                                                                            bigTextStyle.bigText("Congratulation! You got discount offer 50% off on your next appointment");

                                                                                                                                                //builder.setStyle(bigTextStyle);

//                                                                                                                            ////////// set big picture
//                                                                                                                            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
//                                                                                                                            bigPictureStyle.bigPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo));

                                                                                                                                                // builder.setStyle(bigPictureStyle);


                                                                                                                                                /////////// buld notification from builder
                                                                                                                                                Notification notification = builder.build();

                                                                                                                                                ////////pass notification to notification manager
                                                                                                                                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                                                                                                                                                managerCompat.notify(NOTIFY_ID, notification);

                                                                                                                                                ///////// plus ++ for create more notification

                                                                                                                                                NOTIFY_ID++;


                                                                                                                                            }
//                                                                                                                        ////////// create bulder
//                                                                                                                        Notification builder = new NotificationCompat.Builder(context, FCM_ID)
//                                                                                                                                .setContentTitle("Discount Offer")
//                                                                                                                                .setContentText("Congratulation! You got discount offer 50% off on your next appointment")
//                                                                                                                                .setSmallIcon(R.drawable.online)
//                                                                                                                                .setColor(Color.MAGENTA)
//                                                                                                                                .build();
//
//                                                                                                                        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//                                                                                                                        manager.notify(3424, builder);

                                                                                                                                        }
                                                                                                                                    });

                                                                                                                        }


                                                                                                                    }
                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            });

                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                }
                                            }
                                        });

                            }
                        });

            }
        });


    }

    private String getdate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getExpDate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,30);
        String dte = sdf.format(c.getTime()).toString();

        return dte;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_academy, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_subName, txt_experienec, txt_address, txt_pr_hour_price;
        AppCompatButton btn_accept;
        LinearLayout lay_address, lay_pr_hour_price, lay_experience, lay_pay;
        ImageView img_moreinfo, img_hideinfo, imageprofile;
        TextView txtname, txt_number, txt_email;
        RelativeLayout lay_callChat, laycontact, laychat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            laycontact = itemView.findViewById(R.id.laycontact);
            laychat = itemView.findViewById(R.id.laychat);

            txt_address = itemView.findViewById(R.id.txt_address);

            btn_accept = itemView.findViewById(R.id.btn_accept);

            lay_experience = itemView.findViewById(R.id.lay_experience);
            lay_address = itemView.findViewById(R.id.lay_address);
            img_moreinfo = itemView.findViewById(R.id.img_moreinfo);
            img_hideinfo = itemView.findViewById(R.id.img_hideinfo);
            imageprofile = itemView.findViewById(R.id.imageprofile);
            txtname = itemView.findViewById(R.id.txtname);
            txt_number = itemView.findViewById(R.id.txt_number);
            txt_email = itemView.findViewById(R.id.txt_email);
            lay_callChat = itemView.findViewById(R.id.lay_callChat);
            txt_experienec = itemView.findViewById(R.id.txt_experienec);
            lay_pay = itemView.findViewById(R.id.lay_pay);


            lay_pay.setVisibility(View.GONE);
            lay_experience.setVisibility(View.GONE);
            lay_address.setVisibility(View.GONE);
            img_hideinfo.setVisibility(View.GONE);
            lay_callChat.setVisibility(View.GONE);


        }
    }
}
