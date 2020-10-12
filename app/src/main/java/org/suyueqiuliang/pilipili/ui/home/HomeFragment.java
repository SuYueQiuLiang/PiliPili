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

import org.json.JSONException;
import org.json.JSONObject;
import org.suyueqiuliang.pilipili.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HomeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.home_fragment_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        recyclerView.setLayoutManager(gridLayoutManager);
        HomeVideoCardRecyclerViewAdapter adapter = new HomeVideoCardRecyclerViewAdapter(null,null,null,null,null);
        recyclerView.setAdapter(adapter);
        return root;
    }


}

