package org.suyueqiuliang.pilipili.ui.comic;

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

public class ComicFragment extends Fragment {

    private ComicViewModel comicViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        comicViewModel =
                new ViewModelProvider(this).get(ComicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_comic, container, false);
        final TextView textView = root.findViewById(R.id.text_comic);
        comicViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}