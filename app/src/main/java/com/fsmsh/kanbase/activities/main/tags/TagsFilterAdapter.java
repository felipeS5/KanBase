package com.fsmsh.kanbase.activities.main.tags;

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
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.activities.edit.EditActivity;
import com.fsmsh.kanbase.activities.main.home.FragmentsIniciais;
import com.fsmsh.kanbase.model.Tarefa;
import com.fsmsh.kanbase.util.Database;
import com.fsmsh.kanbase.util.DateUtilities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TagsFilterAdapter extends RecyclerView.Adapter<TagsFilterAdapter.MeuVH> {

    private List<Tarefa> tarefas = new ArrayList<>();
    private Context context;
    private TagsFragment parent;

    public TagsFilterAdapter(List<Tarefa> tarefas, Context context, TagsFragment tagsFragment) {
        this.tarefas = tarefas;
        this.context = context;
        this.parent = tagsFragment;
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
            holder.data.setText(context.getString(R.string.item_prazo) + DateUtilities.getFormattedDate(localDate, true, context));
        else
            holder.data.setText( DateUtilities.getFormattedDate(localDate, true, context) );

        // Prioridade
        if (tarefa.getPrioridade() != 4) {
            holder.prioridade.setVisibility(View.VISIBLE);
            if (tarefa.getPrioridade() == 0) {
                holder.prioridade.setText(R.string.prioridade_urgente);
                holder.prioridade.setTextColor(context.getColor(R.color.red));
            } else if (tarefa.getPrioridade() == 1) {
                holder.prioridade.setText(R.string.prioridade_importante);
                holder.prioridade.setTextColor(context.getColor(R.color.orangeRed));
            } else if (tarefa.getPrioridade() == 2) {
                holder.prioridade.setText(R.string.prioridade_media);
                holder.prioridade.setTextColor(context.getColor(R.color.yellow));
            } else if (tarefa.getPrioridade() == 3) {
                holder.prioridade.setText(R.string.prioridade_baixa);
                holder.prioridade.setTextColor(context.getColor(R.color.green));
            }
        }

        // Exibe os alertas
        if (DateUtilities.isAtrazada(tarefa) || DateUtilities.isVencendoHoje(tarefa) || tarefa.getProgresso()==FragmentsIniciais.FINALIZADAS) {
            holder.alertsContainer.setVisibility(View.VISIBLE);

            // Atrazada?
            if (DateUtilities.isAtrazada(tarefa)) {
                holder.lblAtrazada.setBackground(context.getDrawable(R.drawable.bg_small_red));
                holder.lblAtrazada.setVisibility(View.VISIBLE);
                holder.lblAtrazada.setAnimation(AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.blink));
            }

            // Vencendo hoje
            if (DateUtilities.isVencendoHoje(tarefa)) {
                holder.lblVencedo.setBackground(context.getDrawable(R.drawable.bg_small_orange));
                holder.lblVencedo.setVisibility(View.VISIBLE);
                holder.lblVencedo.setAnimation(AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.blink));
            }

            // Missão cumprida?
            if (tarefa.getProgresso() == FragmentsIniciais.FINALIZADAS) {
                holder.lblCumprida.setBackground(context.getDrawable(R.drawable.bg_small_green));
                holder.lblCumprida.setVisibility(View.VISIBLE);
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
        LinearLayout alertsContainer;
        TextView lblAtrazada;
        TextView lblVencedo;
        TextView lblCumprida;
        ConstraintLayout item;

        public MeuVH(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.lblTitulo);
            data = itemView.findViewById(R.id.lblInicio);
            prioridade = itemView.findViewById(R.id.itemViewPrioridade);
            alertsContainer = itemView.findViewById(R.id.item_view_alertas_container);
            lblAtrazada = itemView.findViewById(R.id.item_view_atrazada);
            lblVencedo = itemView.findViewById(R.id.item_view_vencendo);
            lblCumprida = itemView.findViewById(R.id.item_view_cumprida);
            item = itemView.findViewById(R.id.item_main);

            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = ((Activity) context).getMenuInflater();
            inflater.inflate(R.menu.floating_menu, contextMenu);

            contextMenu.add(Menu.NONE, R.id.action_edit, Menu.NONE, R.string.action_editar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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

            contextMenu.add(Menu.NONE, R.id.action_delete, Menu.NONE, R.string.action_deletar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                    int position = getAdapterPosition();

                    // Funcao delete
                    Tarefa tarefa = tarefas.get(position);
                    Database.deleteTarefa(tarefa);

                    parent.start();

                    return true;
                }
            });
        }
    }
}
