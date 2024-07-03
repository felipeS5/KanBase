package com.fsmsh.checkpad.util;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.EditActivity;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.ui.home.AndamentoFragment;
import com.fsmsh.checkpad.ui.home.FinalizadasFragment;
import com.fsmsh.checkpad.ui.home.HomeFragment;

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
    private Context context;
    private HomeFragment homeFragment;
    private AndamentoFragment andamentoFragment;
    private FinalizadasFragment finalizadasFragment;

    public HomeAdapter(List<Tarefa> tarefas, Context context, Fragment fragment) {
        this.tarefas = tarefas;
        this.context = context;
        if (fragment instanceof HomeFragment) this.homeFragment = (HomeFragment) fragment;
        if (fragment instanceof AndamentoFragment) this.andamentoFragment = (AndamentoFragment) fragment;
        if (fragment instanceof FinalizadasFragment) this.finalizadasFragment = (FinalizadasFragment) fragment;
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

        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }


    // View Holder
    public class MeuVH extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView titulo;
        TextView data;
        ConstraintLayout item;

        public MeuVH(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.lblTitulo);
            data = itemView.findViewById(R.id.lblInicio);
            item = itemView.findViewById(R.id.item_main);

            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = ((Activity) context).getMenuInflater();
            inflater.inflate(R.menu.floating_menu, contextMenu);

            contextMenu.add(Menu.NONE, R.id.action_edit, Menu.NONE, "Editar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    int position = getAdapterPosition();

                    // Funcao edit
                    Tarefa tarefa = tarefas.get(position);
                    Intent intent = new Intent(itemView.getContext(), EditActivity.class);
                    intent.putExtra("isNovo", false);
                    intent.putExtra("id", tarefa.getId());
                    itemView.getContext().startActivity(intent);

                    return true;
                }
            });

            contextMenu.add(Menu.NONE, R.id.action_delete, Menu.NONE, "Deletar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    int position = getAdapterPosition();

                    // Funcao delete
                    Tarefa tarefa = tarefas.get(position);
                    Database.deleteTarefa(tarefa);

                    if (homeFragment != null) homeFragment.start();
                    else if (andamentoFragment != null) andamentoFragment.start();
                    else finalizadasFragment.start();

                    return true;
                }
            });
        }
    }
}
