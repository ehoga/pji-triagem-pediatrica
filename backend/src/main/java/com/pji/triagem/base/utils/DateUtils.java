package com.pji.triagem.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final String MASK_DEFAULT = "yyyyMMdd";

    public static LocalDateTime convert(LocalDate date) {
        return LocalDateTime.parse(date.toString() + "T00:00:00.000");
    }

    public static LocalDate convert(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        Instant instant = Instant.ofEpochMilli(dateToConvert.getTime());
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static LocalDateTime convert(String date) {
        return LocalDateTime.parse(date.toString());
    }

    public static LocalDate convert(LocalDateTime data) {
        return data.toLocalDate();
    }

    public static String convert(Object date, String mascara) {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern(MASK_DEFAULT);
        LocalDate data = LocalDate.parse(date.toString(), formatador);
        formatador = DateTimeFormatter.ofPattern(mascara);
        return data.format(formatador);
    }

    public static LocalDate convert(String date, String mascara) {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern(mascara);
        return LocalDate.parse(date.toString(), formatador);
    }

    public static LocalDate convertString(String date) {
        return LocalDate.parse(date.toString());
    }

    public static LocalDateTime convertDataFinal(LocalDate date) {
        return LocalDateTime.parse(date.toString() + "T23:59:59.999");
    }

    public static LocalDate subtractDays(LocalDate date, long amount) {
        return date.minusDays(amount);
    }

    public static Calendar getCalendar(String date, String mascara) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        LocalDate localDate = LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern(mascara));
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar;
    }

    public static String getDataInStringAddDays(int days) {
        LocalDate date = LocalDate.now();
        date = date.plusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String getDataInStringAddDaysPoint(int days) {
        LocalDate date = LocalDate.now();
        date = date.plusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    public static String getDataInStringAddDays(String inputDate, int daysToAdd) {
        LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        date = date.plusDays(daysToAdd);
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String getDataInStringAddDaysPoint(String inputDate, int daysToAdd) {
        LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        date = date.plusDays(daysToAdd);
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String getDataInStringAddMonths(String inputDate, int monthsToAdd) {
        // Parse da data de entrada
        LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Adição de meses
        date = date.plusMonths(monthsToAdd);

        // Verifica se o próximo mês tem 29 ou 30 dias
        int nextMonthLength = date.lengthOfMonth();

        // Ajusta para o último dia do mês se o dia ultrapassar os limites
        if (date.getDayOfMonth() > nextMonthLength) {
            date = date.withDayOfMonth(nextMonthLength);
        }

        // Formata a data de saída
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static Date parseDateString(String dateString)  {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getDataInStringAddMonthsPoint(String inputDate, int monthsToAdd) {
        // Parse da data de entrada
        LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Adição de meses
        date = date.plusMonths(monthsToAdd);

        // Verifica se o próximo mês tem 29 ou 30 dias
        int nextMonthLength = date.lengthOfMonth();

        // Ajusta para o último dia do mês se o dia ultrapassar os limites
        if (date.getDayOfMonth() > nextMonthLength) {
            date = date.withDayOfMonth(nextMonthLength);
        }

        // Formata a data de saída
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    public static String getDataInStringAddMonthsPoint2(String inputDate, int monthsToAdd) {
        // Parse da data de entrada
        LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        // Adição de meses
        date = date.plusMonths(monthsToAdd);

        // Verifica se o próximo mês tem 29 ou 30 dias
        int nextMonthLength = date.lengthOfMonth();

        // Ajusta para o último dia do mês se o dia ultrapassar os limites
        if (date.getDayOfMonth() > nextMonthLength) {
            date = date.withDayOfMonth(nextMonthLength);
        }

        // Formata a data de saída
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String getDataInStringPoint(Date inputDate) {
        // Parse da data de entrada
        LocalDate localDate = inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // Formate a LocalDate para a string desejada
        return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static Date convertDate(String date) {
        return java.sql.Date.valueOf(date);
    }

    public static String convertDateStringFormatBRPoint(String date) {
        DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatoSaida = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate data = LocalDate.parse(date, formatoEntrada);
        return data.format(formatoSaida);
    }


    public static Date convertStringInDate(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        Instant instant = Instant.from(formatter.parse(date));

        LocalDate localDate = LocalDate.from(instant.atZone(ZoneId.of("UTC")));
        return java.sql.Date.valueOf(localDate);
    }

    public static Date convertStringInDate2(String dateparser) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date = sdf.parse(dateparser);
        return java.sql.Date.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static Date convertStringInDate3(String dateparser) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateparser);
        return java.sql.Date.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

    }

    public static Date convertFromWebhookPixWithTimeStamp2(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        try{
            return inputFormat.parse(date);
        }catch (Exception e){
            throw new RuntimeException("Erro ao converter data");
        }

    }

    public static Date converterStringParaData(String dataString) {
        SimpleDateFormat formato = new SimpleDateFormat("dd.MM.yyyy");

        try {
            return formato.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();  // ou trate a exceção conforme necessário
            return null;
        }
    }

    public static Date convertFromWebhookPixWithTimeStamp(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try{
            return inputFormat.parse(date);
        }catch (Exception e){
            throw new RuntimeException("Erro ao converter data");
        }

    }
    public static int getMothByDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) ;
    }

    public static Date getDateByMothAndDayAndYear(int moth, int day, int Year)  {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            return dateFormat.parse(Year + "-" + moth + "-" + day);
        }catch (Exception e) {
            throw new RuntimeException("Erro ao converter data");
        }
    }

    public static boolean compararDatasSemHoras(Date data1, Date data2, ZoneId zoneId) {
        LocalDate localDate1 = data1.toInstant().atZone(zoneId).toLocalDate();
        LocalDate localDate2 = data2.toInstant().atZone(zoneId).toLocalDate();
        return localDate1.isEqual(localDate2);
    }
}
