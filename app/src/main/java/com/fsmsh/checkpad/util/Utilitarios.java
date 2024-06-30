package com.fsmsh.checkpad.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.ui.home.AndamentoFragment;
import com.fsmsh.checkpad.ui.home.FinalizadasFragment;
import com.fsmsh.checkpad.ui.home.HomeFragment;

import java.util.List;

public class Utilitarios {

    HomeFragment homeFragment;
    AndamentoFragment andamentoFragment;
    FinalizadasFragment finalizadasFragment;
    List<Tarefa> tarefas;

    public Utilitarios(Fragment fragment) {
        if (fragment instanceof HomeFragment) this.homeFragment = (HomeFragment) fragment;
        if (fragment instanceof AndamentoFragment) this.andamentoFragment = (AndamentoFragment) fragment;
        if (fragment instanceof FinalizadasFragment) this.finalizadasFragment = (FinalizadasFragment) fragment;
    }

    public void swipe(RecyclerView recyclerView) {
        // todo: fazer bg de item ao mover com cor diferente e texto (se foi para andamento, finalizadas...)
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (homeFragment != null) return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.END);
                else if (andamentoFragment != null) return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START | ItemTouchHelper.END);
                else return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.i("test", "item "+direction);

                mover(viewHolder, direction);

                if (homeFragment != null) homeFragment.start();
                if (andamentoFragment != null) andamentoFragment.start();
                if (finalizadasFragment != null) finalizadasFragment.start();
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    public boolean mover(RecyclerView.ViewHolder viewHolder, int direction) {
        Tarefa tarefa = tarefas.get(viewHolder.getAdapterPosition());

        if (homeFragment != null) {
            tarefa.setProgresso(1);
        } else if (andamentoFragment != null) {
            if (direction == 16) tarefa.setProgresso(0);
            if (direction == 32) tarefa.setProgresso(2);
        } else {
            tarefa.setProgresso(1);
        }

        boolean isMovida = Database.editTarefa(tarefa);

        if (isMovida) return true;
        else return false;
    }


    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }
}
