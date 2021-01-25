package org.suyueqiuliang.pilipili.ui.home;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.suyueqiuliang.pilipili.R;
import org.suyueqiuliang.pilipili.tool.ToolClass;
import org.suyueqiuliang.pilipili.tool.video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {
    static RecyclerView recyclerView;
    static HomeVideoCardRecyclerViewAdapter adapter;
    static GridLayoutManager gridLayoutManager;
    static ArrayList<video> arrayList = new ArrayList<>();
    static boolean is = true;
    static ToolClass toolClass;
    static Activity activity;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        toolClass = new ToolClass();
        recyclerView = root.findViewById(R.id.home_fragment_recycler);
        gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        activity = getActivity();
        adapter = new HomeVideoCardRecyclerViewAdapter(arrayList,activity);
        recyclerView.setAdapter(adapter);
        createAdapter();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
                assert lm != null;
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
    public static void createAdapter(){
        AsyncTask<String, Void, ArrayList<video>> asyncTask = new createAdapter();
        asyncTask.execute(new String[1]);
    }
    private static class createAdapter extends AsyncTask<String, Void, ArrayList<video>> {
        @Override
        protected ArrayList<video> doInBackground(String... voids) {
            try {
                return toolClass.getAppRecommendVideo();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(ArrayList<video> arrayList) {
            HomeFragment.arrayList = arrayList;
            adapter = new HomeVideoCardRecyclerViewAdapter(arrayList,activity);
            recyclerView.setAdapter(adapter);
        }
    }
    private static class flushRecycler extends AsyncTask<String, Void, ArrayList<video>> {
        @Override
        protected ArrayList<video> doInBackground(String... voids) {
            try {
                return toolClass.getAppRecommendVideo();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(ArrayList<video> arrayList) {
            HomeFragment.arrayList = arrayList;
            adapter.setVideo(arrayList);
        }
    }
    public static void flushRecycler(){
        AsyncTask<String, Void, ArrayList<video>> asyncTask = new flushRecycler();
        asyncTask.execute(new String[1]);
    }
    private void addRecycler(){
        AsyncTask<String, Void, ArrayList<video>> asyncTask = new addRecycler();
        asyncTask.execute(new String[1]);
    }
    private static class addRecycler extends AsyncTask<String, Void, ArrayList<video>> {
        @Override
        protected ArrayList<video> doInBackground(String... voids) {
            try {
                return toolClass.getAppRecommendVideo();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(ArrayList<video> arrayList) {
            adapter.addNewVideo(arrayList);
        }
    }
}

