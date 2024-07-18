package com.fsmsh.checkpad.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.EditActivity;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.ui.home.FragmentsIniciais;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MeuVH> {

    private List<Tarefa> tarefas = new ArrayList<>();
    private Context context;
    private FragmentsIniciais fragmentsIniciais;

    public HomeAdapter(List<Tarefa> tarefas, Context context, FragmentsIniciais fragmentsIniciais) {
        this.tarefas = tarefas;
        this.context = context;
        this.fragmentsIniciais = fragmentsIniciais;
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
        LocalDate localDate = null;

        if (!tarefa.getDateStart().equals("")) localDate = DateUtilities.toLocalDate(tarefa.getDateStart());
        else if (!tarefa.getDateLimit().equals("")) localDate = DateUtilities.toLocalDate(tarefa.getDateLimit());

        if ( tarefa.getDateStart().equals("") && !tarefa.getDateLimit().equals("") )
            holder.data.setText("prazo: " + DateUtilities.getFormattedDate(localDate, true));
        else
            holder.data.setText( DateUtilities.getFormattedDate(localDate, true) );

        // Prioridade
        if (tarefa.getPrioridade() != -1) {
            holder.prioridade.setVisibility(View.VISIBLE);
            if (tarefa.getPrioridade() == 0) {
                holder.prioridade.setText("Urgente!!");
                holder.prioridade.setTextColor(context.getColor(R.color.red));
            } else if (tarefa.getPrioridade() == 1) {
                holder.prioridade.setText("Importante!");
                holder.prioridade.setTextColor(context.getColor(R.color.orangeRed));
            } else if (tarefa.getPrioridade() == 2) {
                holder.prioridade.setText("Prioridade média");
                holder.prioridade.setTextColor(context.getColor(R.color.yellow));
            } else if (tarefa.getPrioridade() == 3) {
                holder.prioridade.setText("Baixa prioridade");
                holder.prioridade.setTextColor(context.getColor(R.color.green));
            }
        }


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
        TextView prioridade;
        ConstraintLayout item;

        public MeuVH(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.lblTitulo);
            data = itemView.findViewById(R.id.lblInicio);
            prioridade = itemView.findViewById(R.id.itemViewPrioridade);
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

                    fragmentsIniciais.start();

                    return true;
                }
            });
        }
    }
}
