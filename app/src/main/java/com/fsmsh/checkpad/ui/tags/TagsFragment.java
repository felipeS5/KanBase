package com.fsmsh.checkpad.ui.tags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.PriorityBottomSheet;
import com.fsmsh.checkpad.ui.home.HomeAdapter;
import com.fsmsh.checkpad.util.Database;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class TagsFragment extends Fragment {

    List<String> tagsAtivas;
    boolean[] estadosAtivos = new boolean[] {true, true, true};
    ChipGroup chipGroup;
    List<String> allTags;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        chipGroup = view.findViewById(R.id.chip_group_tags_fragment);
        recyclerView = view.findViewById(R.id.recycler_tags_fragments);
        allTags = Database.getTags();

        tagsAtivas = Database.getTags();
        view.findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeralFilterBottomSheet geralFilterBottomSheet = new GeralFilterBottomSheet(TagsFragment.this, tagsAtivas, estadosAtivos);
                geralFilterBottomSheet.show(getParentFragmentManager(), GeralFilterBottomSheet.TAG);
            }
        });

        setChipTags();

        return view;
    }

    public void setChipTags() {
        chipGroup.removeAllViews();

        for (String s : allTags) {
            Chip chip = new Chip(getContext());
            chip.setCheckable(true);
            chip.setText(s);

            for (String s1 : tagsAtivas) {// verifica se está checado
                if (s.equals(s1)) chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b) {
                        tagsAtivas.add(chip.getText().toString());
                    } else {
                        tagsAtivas.remove(chip.getText().toString());
                    }
                }
            });

            chipGroup.addView(chip);
        }

    }

}