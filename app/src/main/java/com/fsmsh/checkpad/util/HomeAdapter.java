package com.fsmsh.checkpad.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Tarefa;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MeuVH> {

    private List<Tarefa> tarefas = new ArrayList<>();

    public HomeAdapter(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }


    // Metodos
    @NonNull
    @Override
    public MeuVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() )
                .inflate( R.layout.item_view_tarefa, parent, false );
        return new MeuVH( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MeuVH holder, int position) {
        Tarefa tarefa = tarefas.get(position);

        holder.titulo.setText(tarefa.getTarefaNome());
        holder.dataLimite.setText(tarefa.getTimeLimit());

    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }


    // View Holder
    public class MeuVH extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView dataLimite;

        public MeuVH(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.lblTitulo);
            dataLimite = itemView.findViewById(R.id.lblDataLimite);

        }
    }
}
