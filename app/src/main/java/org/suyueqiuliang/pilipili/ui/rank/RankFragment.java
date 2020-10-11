package org.suyueqiuliang.pilipili.ui.rank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.suyueqiuliang.pilipili.R;

public class RankFragment extends Fragment {

    private RankViewModel rankViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rankViewModel =
                new ViewModelProvider(this).get(RankViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rank, container, false);
        final TextView textView = root.findViewById(R.id.text_rank);
        rankViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}