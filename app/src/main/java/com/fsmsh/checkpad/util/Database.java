package com.fsmsh.checkpad.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fsmsh.checkpad.model.Tarefa;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static SQLiteDatabase sql;
    private static final int PROGRESS_NAO_INICIADO = 0;
    private static final int PROGRESS_INICIADO = 1;
    private static final int PROGRESS_COMPLETO = 2;
    private static final int PROGRESS_FALHO = -1;
    private Context context;

    public Database(Context context) {
        this.context = context;

        sql = context.openOrCreateDatabase("Dados.db", Context.MODE_PRIVATE, null);
        sql.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefaNome VARCHAR, descricao VARCHAR, progresso INT(1), timeStart VARCHAR, timeLimit VARCHAR, categoria VARCHAR, prioridade INT(1))");
    }

    public static boolean addTarefa(Tarefa tarefa) {
        try {
            String campos = "tarefaNome, descricao, progresso, timeStart, timeLimit, categoria, prioridade";
            String valores = "'"+ tarefa.getTarefaNome() +"', '"+tarefa.getDescricao() +"', "+ tarefa.getProgresso() +", '"+ tarefa.getTimeStart() +"', '"+ tarefa.getTimeLimit() +"', '"+ tarefa.getCategoria() +"', "+ tarefa.getPrioridade();

            sql.execSQL("INSERT INTO tarefas ("+ campos +") VALUES (" +valores+ ")");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Tarefa getTarefa(int id) {
        Tarefa tarefa = new Tarefa();
        String query = "id, tarefaNome, descricao, progresso, timeStart, timeLimit, categoria, prioridade";

        try {
            Cursor cursor = sql.rawQuery("SELECT " +query+ " FROM tarefas WHERE id="+ id +"", null);
            cursor.moveToFirst();

            tarefa.setId( cursor.getInt(0) );
            tarefa.setTarefaNome( cursor.getString(1) );
            tarefa.setDescricao( cursor.getString(2) );
            tarefa.setProgresso( cursor.getInt(3) );
            tarefa.setTimeStart( cursor.getString(4) );
            tarefa.setTimeLimit( cursor.getString(5) );
            tarefa.setCategoria( cursor.getString(6) );
            tarefa.setPrioridade( cursor.getInt(7) );

        }catch (Exception e) {
            e.printStackTrace();
        }

        return tarefa;
    }

    public static List<Tarefa> getTarefas(int progress) {
        List<Tarefa> tarefas = new ArrayList<>();
        String query = "id, tarefaNome, descricao, progresso, timeStart, timeLimit, categoria, prioridade";

        try {

            Cursor cursor = sql.rawQuery("SELECT " +query+ " FROM tarefas", null);
            cursor.moveToFirst();

            while (cursor.getPosition() < cursor.getCount()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId( cursor.getInt(0) );
                tarefa.setTarefaNome( cursor.getString(1) );
                tarefa.setDescricao( cursor.getString(2) );
                tarefa.setProgresso( cursor.getInt(3) );
                tarefa.setTimeStart( cursor.getString(4) );
                tarefa.setTimeLimit( cursor.getString(5) );
                tarefa.setCategoria( cursor.getString(6) );
                tarefa.setPrioridade( cursor.getInt(7) );

                if (progress == tarefa.getProgresso()) {
                    tarefas.add(tarefa);
                }

                cursor.moveToNext();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return tarefas;
    }

    public static boolean editTarefa(Tarefa t) {

        String sqlAtt = String.format("tarefaNome = '%s', descricao = '%s', progresso = %d, timeStart = '%s', timeLimit = '%s', categoria = '%s', prioridade = %d",
                t.getTarefaNome(), t.getDescricao(), t.getProgresso(), t.getTimeStart(), t.getTimeLimit(), t.getCategoria(), t.getPrioridade() );

        try {
            sql.execSQL("UPDATE tarefas SET " + sqlAtt + " WHERE id = "+ t.getId() +"");

            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean deleteTarefa(Tarefa t) {

        try {
            sql.execSQL("DELETE FROM tarefas WHERE id = " + t.getId());

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
