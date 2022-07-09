package dev.sma.uos.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import dev.sma.uos.Common;
import dev.sma.uos.R;
import dev.sma.uos.Utils;
import dev.sma.uos.WrapContentLinearLayoutManager;
import dev.sma.uos.adapter.Adapter_DoctorsList;
import dev.sma.uos.model.Model_DoctorsList;


public class FragmentOfflineDoctor extends Fragment {

    Context context;
    Activity activity;

    FirebaseFirestore firestore;

    RecyclerView rv_list_subjects;
    Adapter_DoctorsList adapterSubjectList;
    FirestoreRecyclerOptions<Model_DoctorsList> modelSubjectListFirestoreRecyclerOptions;


    Utils utils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offline_doctore, container, false);

        rv_list_subjects = view.findViewById(R.id.list_subjects);
        rv_list_subjects.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        ///////// get context
        context = getContext();
        activity = (Activity) view.getContext();
        firestore = FirebaseFirestore.getInstance();


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


        Query query =
                firestore.collection(Common.DOCTOR)
                        .whereEqualTo(Common.DOCTOR_ONLINE_STATUS, "Offline");

        modelSubjectListFirestoreRecyclerOptions = new FirestoreRecyclerOptions
                .Builder<Model_DoctorsList>()
                .setQuery(query, Model_DoctorsList.class)
                .setLifecycleOwner(this)
                .build();

        adapterSubjectList = new Adapter_DoctorsList(modelSubjectListFirestoreRecyclerOptions, context);
        rv_list_subjects.setAdapter(adapterSubjectList);


    }


    @Override
    public void onStart() {
        super.onStart();
        adapterSubjectList.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterSubjectList.stopListening();
    }

}