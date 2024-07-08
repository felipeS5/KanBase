package com.fsmsh.checkpad.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtilities {

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
}
