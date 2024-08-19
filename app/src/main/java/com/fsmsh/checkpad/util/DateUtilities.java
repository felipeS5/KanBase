package com.fsmsh.checkpad.util;

import android.content.Context;
import android.util.Log;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.ModalBottomSheet;
import com.fsmsh.checkpad.activities.main.home.FragmentsIniciais;
import com.fsmsh.checkpad.model.Tarefa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtilities {

    public static String getSaudacoes(Context context) {
        LocalTime localTime = LocalTime.now();

        if (localTime.getHour() < 12) return context.getString(R.string.bom_dia);
        if (localTime.getHour() < 18 ) return context.getString(R.string.boa_tarde);
        else return context.getString(R.string.boa_noite);
    }

    public static boolean isAtrazada(Tarefa tarefa) {
        if (tarefa.getProgresso() == FragmentsIniciais.FINALIZADAS) return false;

        LocalDate dateNow = LocalDate.now();

        // Atrazo no dateStart
        if (!tarefa.getDateStart().equals("")) { // Verifica se tem startDate
            LocalDate dateStart = toLocalDate(tarefa.getDateStart());

            if (dateNow.isAfter(dateStart) && (tarefa.getProgresso() == FragmentsIniciais.NOVAS)) {
                return true;
            }
        }

        // Atrazo no prazo limite
        if (!tarefa.getDateLimit().equals("")) { // Verifica se tem limitDate
            LocalDate dateLimit = toLocalDate(tarefa.getDateLimit());

            if (dateNow.isAfter(dateLimit)) {
                return true;
            }
        }

        return false;

    }

    public static boolean isVencendoHoje(Tarefa tarefa) {
        if (tarefa.getDateLimit().equals("")) return false;

        LocalDate dateLimit = toLocalDate(tarefa.getDateLimit());
        LocalDate dateNow = LocalDate.now();

        if (dateLimit.isEqual(dateNow) && (tarefa.getProgresso() != FragmentsIniciais.FINALIZADAS)) {
            return true;
        }

        return false;

    }

    public static boolean isToday(LocalDate compare, int plusDay) {
        LocalDate hoje = LocalDate.now();
        LocalDate dia = hoje.plusDays(plusDay);

        if (compare.toString().equals(dia.toString())) {
            return true;
        } else {
            return false;
        }

    }

    public static String getFormattedDate(LocalDate localDate, boolean abreviar) {
        if (localDate == null) return "";

        // Variáveis padrões
        DateTimeFormatter formatoData;

        if (abreviar) formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        else formatoData = DateTimeFormatter.ofPattern("dd 'de' MMM 'de' yyyy");

        String dataFormatada = localDate.format(formatoData);

        if ( DateUtilities.isToday(localDate, 0) ) dataFormatada = "Hoje";
        else if ( DateUtilities.isToday(localDate, 1) ) dataFormatada = "Amanhã";
        else if ( DateUtilities.isToday(localDate, -1) ) dataFormatada = "Ontem";

        return dataFormatada;
    }

    public static LocalDate toLocalDate(String stringDate) {

        String[] dtTemp = stringDate.split("-");
        int[] dtI = {Integer.parseInt(dtTemp[0]), Integer.parseInt(dtTemp[1]), Integer.parseInt(dtTemp[2])};

        return LocalDate.of(dtI[0], dtI[1], dtI[2]);
    }

    public static LocalTime toLocalTime(String stringTime) {

        String[] tiTemp = stringTime.split(":");
        int[] tiI = {Integer.parseInt(tiTemp[0]), Integer.parseInt(tiTemp[1])};

        return LocalTime.of(tiI[0], tiI[1]);
    }

    public static LocalDateTime getNextTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        int minutoAtual = localDateTime.getMinute();
        int minutoPlus = 0;
        boolean addHour = false;

        if (minutoAtual >= 0 && minutoAtual < 15) {
            minutoPlus = 30 - minutoAtual;
        } else if (minutoAtual >= 15 && minutoAtual < 30) {
            minutoPlus = 45 - minutoAtual;
        } else if (minutoAtual >= 30 && minutoAtual < 45) {
            minutoPlus = 0 - minutoAtual;
            addHour = true;
        } else if (minutoAtual >= 45 && minutoAtual <= 59) {
            minutoPlus = 15 - minutoAtual;
            addHour = true;
        }

        if (addHour) localDateTime = localDateTime.plusMinutes(minutoPlus+60);
        else localDateTime = localDateTime.plusMinutes(minutoPlus);

        return localDateTime;
    }

    public static LocalDateTime getNextLimitTime(ModalBottomSheet parent) {

        LocalDateTime localDateTime;

        if (parent.parent.dateStart == null) {
            localDateTime = getNextTime().plusDays(1);
        } else {
            localDateTime = parent.parent.dateStart.atTime(parent.parent.timeStart);
            localDateTime = localDateTime.plusHours(1);
        }

        return localDateTime;
    }
}
