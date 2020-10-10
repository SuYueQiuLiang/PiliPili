package org.suyueqiuliang.pilipili.ui.later;

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
import androidx.lifecycle.ViewModelProviders;

import org.suyueqiuliang.pilipili.R;

public class LaterFragment extends Fragment {

    private LaterViewModel laterViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        laterViewModel =
                new ViewModelProvider(this).get(LaterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_later, container, false);
        final TextView textView = root.findViewById(R.id.text_later);
        laterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}