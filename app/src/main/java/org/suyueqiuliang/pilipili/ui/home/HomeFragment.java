package org.suyueqiuliang.pilipili.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.suyueqiuliang.pilipili.MainActivity;
import org.suyueqiuliang.pilipili.R;
import org.suyueqiuliang.pilipili.tool.video;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    static RecyclerView recyclerView;
    static HomeVideoCardRecyclerViewAdapter adapter;
    GridLayoutManager gridLayoutManager;
    static ArrayList<video> arrayList = new ArrayList<>();
    static boolean is = true;
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == totalItemCount - 1
                        && visibleItemCount > 0&&is) {
                    is = false;
                    //加载更多
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.addAndShowRecommendVideo();
                }
            }
        });
        return root;
    }

    public void flushRecycler(ArrayList<video> arrayList){
        //adapter.addNewVideo(arrayList);
        HomeFragment.arrayList = arrayList;
        adapter = new HomeVideoCardRecyclerViewAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
    public void addRecycler(ArrayList<video> arrayList){
        //HomeFragment.arrayList.addAll(arrayList);
        adapter.addNewVideo(arrayList);
        is = true;
    }
}

