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
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.ui.home.HomeAdapter;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.DateUtilities;
import com.fsmsh.checkpad.util.Sort;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
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
        filtro();

        // Recycler
        adapter = new TagsFilterAdapter( tarefas, getActivity(), this );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void filtro() {
        List<Tarefa> tarefasFiltradas = new ArrayList<>();

        for (Tarefa tarefa : tarefas) {
            boolean isProgressOK = false;
            boolean isTagOK = false;

            // estado notStarted
            if ( (tarefa.getProgresso() == Database.PROGRESS_NAO_INICIADO) && estadosAtivos[Database.PROGRESS_NAO_INICIADO]) {
                isProgressOK = true;
            }
            // estado started
            if ( (tarefa.getProgresso() == Database.PROGRESS_INICIADO) && estadosAtivos[Database.PROGRESS_INICIADO]) {
                isProgressOK = true;
            }
            // estado finished
            if ( (tarefa.getProgresso() == Database.PROGRESS_COMPLETO) && estadosAtivos[Database.PROGRESS_COMPLETO]) {
                isProgressOK = true;
            }

            // Checagem de tags
            // todo: add opção de exibir tarefas sem tags no filterBottomSheet
            if (isProgressOK) {
                String[] tagsArr = tarefa.getCategoria().split("‖");
                List<String> tagsDaTarefa = new ArrayList<>();
                for (String s : tagsArr) if (!s.equals("")) tagsDaTarefa.add(s);

                if ((tagsDaTarefa.size() == 0) && aceitarSemTags) isTagOK = true;

                for (String tagTarefa : tagsDaTarefa) {
                    for (String tagAtiva : tagsAtivas) {
                        if (tagTarefa.equals(tagAtiva)) isTagOK = true;
                    }
                }

            }

            // Checagem para ver se passou pelo filtro
            if (isProgressOK && isTagOK) tarefasFiltradas.add(tarefa);

        }

        tarefas = tarefasFiltradas;

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