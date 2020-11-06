package org.suyueqiuliang.pilipili.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.suyueqiuliang.pilipili.MainActivity;
import org.suyueqiuliang.pilipili.R;
import org.suyueqiuliang.pilipili.tool.ToolClass;
import org.suyueqiuliang.pilipili.tool.video;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    static RecyclerView recyclerView;
    @SuppressLint("StaticFieldLeak")
    static HomeVideoCardRecyclerViewAdapter adapter;
    GridLayoutManager gridLayoutManager;
    static ArrayList<video> arrayList = new ArrayList<>();
    static boolean is = true;
    ToolClass toolClass;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        toolClass = new ToolClass();
        recyclerView = root.findViewById(R.id.home_fragment_recycler);
        gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,50);
        if(arrayList.size() != 0) {
            adapter = new HomeVideoCardRecyclerViewAdapter(arrayList,getActivity());
            recyclerView.setAdapter(adapter);
        }else{
            flushRecycler();
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
                    addRecycler();
                    is = true;
                }
            }
        });
        return root;
    }

    private class flushRecycler extends AsyncTask<String, Void, ArrayList<video>> {
        Bitmap bitmap;
        @Override
        protected ArrayList<video> doInBackground(String... voids) {
            ArrayList<video> arrayList = toolClass.getAppRecommendVideo();
            return arrayList;
        }

        protected void onPostExecute(ArrayList<video> arrayList) {
            HomeFragment.arrayList = arrayList;
            adapter = new HomeVideoCardRecyclerViewAdapter(arrayList,getActivity());
            recyclerView.setAdapter(adapter);
        }
    }
    public void flushRecycler(){
        AsyncTask asyncTask = new flushRecycler();
        asyncTask.execute(new String[1]);
    }
    public void addRecycler(){
        AsyncTask asyncTask = new addRecycler();
        asyncTask.execute(new String[1]);
    }
    private class addRecycler extends AsyncTask<String, Void, ArrayList<video>> {
        Bitmap bitmap;
        @Override
        protected ArrayList<video> doInBackground(String... voids) {
            ArrayList<video> arrayList = toolClass.getAppRecommendVideo();
            return arrayList;
        }

        protected void onPostExecute(ArrayList<video> arrayList) {
            adapter.addNewVideo(arrayList);
        }
    }
}

