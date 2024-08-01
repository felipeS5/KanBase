package com.fsmsh.checkpad.ui.tags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.PriorityBottomSheet;

public class TagsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        view.findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeralFilterBottomSheet geralFilterBottomSheet = new GeralFilterBottomSheet(TagsFragment.this);
                geralFilterBottomSheet.show(getParentFragmentManager(), GeralFilterBottomSheet.TAG);
            }
        });

        return view;
    }

}