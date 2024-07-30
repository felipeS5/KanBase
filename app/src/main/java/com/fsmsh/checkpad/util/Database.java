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
        sql.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefaNome VARCHAR, descricao VARCHAR, progresso INT(1), dateStart VARCHAR, timeStart VARCHAR, dateLimit VARCHAR, timeLimit VARCHAR, categoria VARCHAR, prioridade INT(1))");
        sql.execSQL("CREATE TABLE IF NOT EXISTS tags (tagName VARCHAR)");
    }

    public static boolean addTarefa(Tarefa tarefa) {
        try {
            String campos = "tarefaNome, descricao, progresso, dateStart, timeStart, dateLimit, timeLimit, categoria, prioridade";
            String valores = "'"+ tarefa.getTarefaNome() +"', '"+tarefa.getDescricao() +"', "+ tarefa.getProgresso() +", '"+ tarefa.getDateStart() +"', '"+ tarefa.getTimeStart() +"', '"+ tarefa.getDateLimit() +"', '"+ tarefa.getTimeLimit() +"', '"+ tarefa.getCategoria() +"', "+ tarefa.getPrioridade();

            sql.execSQL("INSERT INTO tarefas ("+ campos +") VALUES (" +valores+ ")");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Tarefa getTarefa(int id) {
        Tarefa tarefa = new Tarefa();
        String query = "id, tarefaNome, descricao, progresso, dateStart, timeStart, dateLimit, timeLimit, categoria, prioridade";

        try {
            Cursor cursor = sql.rawQuery("SELECT " +query+ " FROM tarefas WHERE id="+ id +"", null);
            cursor.moveToFirst();

            tarefa.setId( cursor.getInt(0) );
            tarefa.setTarefaNome( cursor.getString(1) );
            tarefa.setDescricao( cursor.getString(2) );
            tarefa.setProgresso( cursor.getInt(3) );
            tarefa.setDateStart( cursor.getString(4) );
            tarefa.setTimeStart( cursor.getString(5) );
            tarefa.setDateLimit( cursor.getString(6) );
            tarefa.setTimeLimit( cursor.getString(7) );
            tarefa.setCategoria( cursor.getString(8) );
            tarefa.setPrioridade( cursor.getInt(9) );

        }catch (Exception e) {
            e.printStackTrace();
        }

        return tarefa;
    }

    public static List<Tarefa> getTarefas(int progress) {
        List<Tarefa> tarefas = new ArrayList<>();
        String query = "id, tarefaNome, descricao, progresso, dateStart, timeStart, dateLimit, timeLimit, categoria, prioridade";

        try {

            Cursor cursor = sql.rawQuery("SELECT " +query+ " FROM tarefas", null);
            cursor.moveToFirst();

            while (cursor.getPosition() < cursor.getCount()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId( cursor.getInt(0) );
                tarefa.setTarefaNome( cursor.getString(1) );
                tarefa.setDescricao( cursor.getString(2) );
                tarefa.setProgresso( cursor.getInt(3) );
                tarefa.setDateStart( cursor.getString(4) );
                tarefa.setTimeStart( cursor.getString(5) );
                tarefa.setDateLimit( cursor.getString(6) );
                tarefa.setTimeLimit( cursor.getString(7) );
                tarefa.setCategoria( cursor.getString(8) );
                tarefa.setPrioridade( cursor.getInt(9) );

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

        String sqlAtt = String.format("tarefaNome = '%s', descricao = '%s', progresso = %d, dateStart = '%s', timeStart = '%s', dateLimit = '%s', timeLimit = '%s', categoria = '%s', prioridade = %d",
                t.getTarefaNome(), t.getDescricao(), t.getProgresso(), t.getDateStart(), t.getTimeStart(), t.getDateLimit(), t.getTimeLimit(), t.getCategoria(), t.getPrioridade() );

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

    // Categorias
    public static boolean addTag(String tag) {
        try {
            sql.execSQL("INSERT INTO tags (tagName) VALUES ('"+ tag +"')");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getTags() {
        List<String> tags = new ArrayList<>();

        try {
            Cursor cursor = sql.rawQuery("SELECT tagName FROM tags", null);
            cursor.moveToFirst();

            while (cursor.getPosition() < cursor.getCount()) {

                tags.add(0, cursor.getString(0));

                cursor.moveToNext();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return tags;
    }

    public static boolean deleteTag(String tag) {
        try {
            sql.execSQL("DELETE FROM tags WHERE tagName = " + tag);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
