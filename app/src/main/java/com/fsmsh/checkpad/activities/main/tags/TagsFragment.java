package com.fsmsh.checkpad.activities.main.tags;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.main.MainActivity;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.Sort;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class TagsFragment extends Fragment {

    List<String> tagsAtivas;
    boolean aceitarSemTags = true;
    boolean[] estadosAtivos = new boolean[] {true, true, true};
    ChipGroup chipGroup;
    List<String> allTags;
    RecyclerView recyclerView;

    List<Tarefa> tarefas;
    TagsFilterAdapter adapter;
    MainActivity parent;

    public TagsFragment(MainActivity parent) {
        this.parent = parent;
    }

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

    public void start() {
        tarefas = Database.getTarefas(Database.PROGRESS_TODOS);
        tarefas = Sort.sortByCreation(tarefas, Sort.ORDEM_DECRESCENTE);
        tarefas = Sort.filtrar(tarefas, estadosAtivos, tagsAtivas, aceitarSemTags);
        autoClassify();
        parent.adjustHeader();

        // Recycler
        adapter = new TagsFilterAdapter( tarefas, getActivity(), this );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void autoClassify() {
        SharedPreferences preferences = getActivity().getSharedPreferences("classify.pref", getContext().MODE_PRIVATE);

        if (preferences.contains("classifyType")) {
            String classifyType = preferences.getString("classifyType", "default");
            int ordem = preferences.getInt("ordem", 0);

            if (classifyType.equals("priority")) {
                tarefas = Sort.sortByPriority(tarefas, ordem);
            } else if (classifyType.equals("creation")) {
                tarefas = Sort.sortByCreation(tarefas, ordem);
            }

        }
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

                    start();
                }
            });

            chipGroup.addView(chip);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }
}