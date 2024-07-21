package com.fsmsh.checkpad.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.HomeAdapter;

import java.util.List;

public class FragmentsIniciais extends Fragment {

    private RecyclerView recyclerView;
    private List<Tarefa> tarefas;
    private HomeAdapter homeAdapter;
    private int PROGRESSO;
    private final int NAO_INICIADAS = 0;
    private final int INICIADAS = 1;
    private final int FINALIZADAS = 2;

    public FragmentsIniciais(int PROGRESSO) {
        this.PROGRESSO = PROGRESSO;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipe();

        return view;
    }

    public void start() {
        tarefas = Database.getTarefas(PROGRESSO);

        // Recycler
        homeAdapter = new HomeAdapter( tarefas, getActivity(), this );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(homeAdapter);

    }

    public void start(List<Tarefa> tarefas) {
        this.tarefas = tarefas;

        // Recycler
        homeAdapter = new HomeAdapter( tarefas, getActivity(), this );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(homeAdapter);

    }

    public void swipe() {
        // todo: fazer bg de item ao mover com cor diferente e texto (se foi para andamento, finalizadas...)
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (PROGRESSO == NAO_INICIADAS) return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.END);
                else if (PROGRESSO == INICIADAS) return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START | ItemTouchHelper.END);
                else return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mover(viewHolder, direction);

                start();
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    public boolean mover(RecyclerView.ViewHolder viewHolder, int direction) {
        Tarefa tarefa = tarefas.get(viewHolder.getAdapterPosition());

        if (PROGRESSO == NAO_INICIADAS) {
            tarefa.setProgresso(1);
        } else if (PROGRESSO == INICIADAS) {
            if (direction == 16) tarefa.setProgresso(0);
            if (direction == 32) tarefa.setProgresso(2);
        } else {
            tarefa.setProgresso(1);
        }

        boolean isMovida = Database.editTarefa(tarefa);

        if (isMovida) return true;
        else return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }
}