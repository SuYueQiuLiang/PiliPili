package org.suyueqiuliang.pilipili.ui.home;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.suyueqiuliang.pilipili.MainActivity;
import org.suyueqiuliang.pilipili.R;
import org.suyueqiuliang.pilipili.video;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    static RecyclerView recyclerView;
    static HomeVideoCardRecyclerViewAdapter adapter;
    GridLayoutManager gridLayoutManager;
    static ArrayList<video> arrayList = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.home_fragment_recycler);
        gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        if(arrayList.size() != 0) {
            adapter = new HomeVideoCardRecyclerViewAdapter(arrayList);
            recyclerView.setAdapter(adapter);
        }
        return root;
    }

    public void flushRecycler(ArrayList<video> arrayList){
        //adapter.addNewVideo(arrayList);
        this.arrayList = arrayList;
        adapter = new HomeVideoCardRecyclerViewAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}

