package com.fsmsh.checkpad.util;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.EditActivity;
import com.fsmsh.checkpad.model.Tarefa;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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

        // Local Date
        LocalDate timeStart = Instant.ofEpochMilli(Long.parseLong(tarefa.getTimeStart()))
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);
        LocalDate ontem = hoje.minusDays(1);
        String dataInicial = timeStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        if (timeStart.toString().equals(hoje.toString())) dataInicial = "Hoje";
        else if (timeStart.toString().equals(amanha.toString())) dataInicial = "Amanhã";
        else if (timeStart.toString().equals(ontem.toString())) dataInicial = "Ontem";
        holder.data.setText(dataInicial);


        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Funcao edit
                Intent intent = new Intent(holder.itemView.getContext(), EditActivity.class);
                intent.putExtra("isNovo", false);
                intent.putExtra("id", tarefa.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }


    // View Holder
    public class MeuVH extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView data;
        ConstraintLayout item;

        public MeuVH(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.lblTitulo);
            data = itemView.findViewById(R.id.lblInicio);
            item = itemView.findViewById(R.id.item_main);

        }
    }
}
