package com.fsmsh.checkpad.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.HomeAdapter;
import com.fsmsh.checkpad.util.Utilitarios;

import java.util.List;


public class AndamentoFragment extends Fragment {

    RecyclerView recyclerView;
    TextView textView;
    Utilitarios utilitarios;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_andamento, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        textView = view.findViewById(R.id.textView2);
        utilitarios = new Utilitarios(this);

        return view;
    }

    public void start() {
        List<Tarefa> tarefas = Database.getTarefas(1);
        utilitarios.setTarefas(tarefas);

        // Recycler
        HomeAdapter homeAdapter = new HomeAdapter( tarefas );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(homeAdapter);

        textView.setText(this.getChildFragmentManager().toString());

        utilitarios.swipe(recyclerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }
}