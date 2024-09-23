package com.fsmsh.checkpad.activities.main.home;

import android.content.SharedPreferences;
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
import com.fsmsh.checkpad.activities.main.MainActivity;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.FirebaseHelper;
import com.fsmsh.checkpad.util.MyPreferences;
import com.fsmsh.checkpad.util.Sort;

import java.util.List;

public class FragmentsIniciais extends Fragment {

    private RecyclerView recyclerView;
    private List<Tarefa> tarefas;
    private HomeAdapter homeAdapter;
    private int PROGRESSO;
    public static final int NOVAS = 0;
    public static final int INICIADAS = 1;
    public static final int FINALIZADAS = 2;
    MainActivity parent;

    public FragmentsIniciais(int PROGRESSO, MainActivity parent) {
        this.PROGRESSO = PROGRESSO;
        this.parent = parent;
    }

    public FragmentsIniciais() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipe();

        return view;
    }

    public void start() {
        new MyPreferences(getContext());
        tarefas = Database.getTarefas(PROGRESSO);
        autoClassify();
        if (parent!=null) {
            parent.setBadges();
            parent.adjustHeader();
        }

        // Recycler
        homeAdapter = new HomeAdapter( tarefas, getActivity(), this );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(homeAdapter);
    }

    public void swipe() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (PROGRESSO == NOVAS) return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.END);
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

        if (PROGRESSO == NOVAS) {
            tarefa.setProgresso(1);
        } else if (PROGRESSO == INICIADAS) {
            if (direction == 16) tarefa.setProgresso(0);
            if (direction == 32) tarefa.setProgresso(2);
        } else {
            tarefa.setProgresso(1);
        }

        boolean isMovida = Database.editTarefa(tarefa);

        if (isMovida) {
            MyPreferences.setSincronizado(false);
            parent.firebaseHelper.atualizarRemoto();
            return true;
        } else {
            return false;
        }
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

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

}