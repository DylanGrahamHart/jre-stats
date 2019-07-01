package com.jrestats.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {

    public static String formatNumber(Integer numberToFormat) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(numberToFormat.longValue());
    }

    public static String formatNumberAbbr(String numberToFormat) {
        String formattedNumber = numberToFormat;
        float number = Float.parseFloat(numberToFormat);

        if (numberToFormat.length() > 9) {
            formattedNumber = String.format("%.1f", (Math.floor(number / Math.pow(10, 9)))) + 'B';
        } else if (numberToFormat.length() > 6) {
            formattedNumber = String.format("%.1f", Math.floor((number / Math.pow(10, 6)))) + 'M';
        } else if (numberToFormat.length() > 3) {
            formattedNumber = String.format("%.0f", Math.floor((number / Math.pow(10, 3)))) + 'K';
        }

        return formattedNumber;
    }

    public static String formatDate(String dateToFormat) {
        String formattedDate = dateToFormat;

        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = inputDateFormat.parse(dateToFormat);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-");

            formattedDate = inputDateFormat.parse(dateToFormat).toString();
        } catch (ParseException e) {}

        return formattedDate;
    }

}
