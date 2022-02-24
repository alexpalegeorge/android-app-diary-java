package com.example.paindiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiary.adapter.RecyclerViewAdapter;
import com.example.paindiary.databinding.FragmentRecordBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainDiaryViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FragmentRecord extends Fragment {
    private FragmentRecordBinding recordBinding;
    private PainDiaryViewModel painDiaryViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private LiveData<List<PainRecord>> painRecordList;
    private RecyclerViewAdapter adapter;

    public FragmentRecord(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recordBinding = FragmentRecordBinding.inflate(inflater, container, false);
        View view = recordBinding.getRoot();

        painDiaryViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainDiaryViewModel.class);


        painDiaryViewModel.getAllPainRecords().observe(getViewLifecycleOwner(),
                new Observer<List<PainRecord>>() {
                    public void onChanged(@Nullable final List<PainRecord> painRecords) {
                        //Recycler view (updates list here)
                        painRecordList = painDiaryViewModel.getAllPainRecords();
                        adapter = new RecyclerViewAdapter(painRecordList);

                        recordBinding.recyclerView.addItemDecoration(new
                                DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                        recordBinding.recyclerView.setAdapter(adapter);

                        layoutManager = new LinearLayoutManager(getContext());
                        recordBinding.recyclerView.setLayoutManager(layoutManager);
                    }
                });
        

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recordBinding = null;
    }
}
