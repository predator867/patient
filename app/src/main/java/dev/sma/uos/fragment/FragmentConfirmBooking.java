package dev.sma.uos.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dev.sma.uos.Common;
import dev.sma.uos.R;
import dev.sma.uos.Utils;
import dev.sma.uos.WrapContentLinearLayoutManager;
import dev.sma.uos.adapter.AdapterConfirmBooking;
import dev.sma.uos.model.Model_DoctorsList;

public class FragmentConfirmBooking extends Fragment {

    Context context;
    Activity activity;

    FirebaseFirestore firestore;

    RecyclerView rv_list_subjects;
    AdapterConfirmBooking adapterSubjectList;
    ArrayList<Model_DoctorsList> modelBookingOrderArrayList;


    Utils utils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_booking, container, false);

        rv_list_subjects = view.findViewById(R.id.list_subjects);
        rv_list_subjects.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        ///////// get context
        context = getContext();
        activity = (Activity) view.getContext();
        firestore = FirebaseFirestore.getInstance();

        modelBookingOrderArrayList = new ArrayList<>();


        utils = new Utils(getContext());

        /////////////  fetch added Subjects
        fetchSubjects();

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchSubjects();
                pullToRefresh.setRefreshing(false);
            }
        });


        return view;
    }


    private void fetchSubjects() {

        utils.startLoading();

        if (modelBookingOrderArrayList.size() > 0) {
            modelBookingOrderArrayList.clear();
        }

        firestore.collection(Common.DOCTOR)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot query : task.getResult()) {

                                firestore.collection(Common.DOCTOR)
                                        .document(query.getId())
                                        .collection(Common.APPOINTMENT)
                                        .whereEqualTo(Common.PATIENT_ID, utils.getToken())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {

                                                    for (QueryDocumentSnapshot query : task.getResult()) {

                                                        if (query.getString(Common.REQUEST_STATUS).equals("accept")) {

                                                            firestore.collection(Common.DOCTOR)
                                                                    .document(query.getString(Common.DOCTOR_ID))
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful() && task.getResult().exists()) {

                                                                                DocumentSnapshot documentSnapshot = task.getResult();

                                                                                Model_DoctorsList modelDoctorsList = new Model_DoctorsList();

                                                                                modelDoctorsList.setAddress(documentSnapshot.getString(Common.DOCTOR_ADDRESS));
                                                                                modelDoctorsList.setAge(documentSnapshot.getString(Common.DOCTOR_AGE));
                                                                                modelDoctorsList.setEmail(documentSnapshot.getString(Common.DOCTOR_EMAIL));
                                                                                modelDoctorsList.setName(documentSnapshot.getString(Common.DOCTOR_NAME));
                                                                                modelDoctorsList.setDoctor_pic(documentSnapshot.getString(Common.DOCTOR_PIC));
                                                                                modelDoctorsList.setNumber(documentSnapshot.getString(Common.DOCTOR_NUMBER));
                                                                                modelDoctorsList.setExperience(documentSnapshot.getString(Common.DOCTOR_EXPERIENCE));
                                                                                modelDoctorsList.setTime(query.getString("time"));


                                                                                modelBookingOrderArrayList.add(modelDoctorsList);

                                                                                adapterSubjectList = new AdapterConfirmBooking(modelBookingOrderArrayList, context);
                                                                                rv_list_subjects.setAdapter(adapterSubjectList);
                                                                                adapterSubjectList.notifyDataSetChanged();

                                                                                utils.endLoading();

                                                                            } else {
                                                                                utils.endLoading();
                                                                            }
                                                                        }
                                                                    });

                                                        }

                                                    }


                                                } else {
                                                    utils.endLoading();
                                                }
                                            }
                                        });

                            }
                        } else {
                            utils.endLoading();
                        }
                    }
                });


    }

}