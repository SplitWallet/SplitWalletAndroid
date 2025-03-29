package com.example.splitwallet.models;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTypeAdapter extends TypeAdapter<Date> {
    private final SimpleDateFormat[] dateFormats = {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault()),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    };

    public DateTypeAdapter() {
        for (SimpleDateFormat format : dateFormats) {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(dateFormats[0].format(value));
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        try {
            String dateString = in.nextString();
            // Пробуем все форматы по очереди
            for (SimpleDateFormat format : dateFormats) {
                try {
                    return format.parse(dateString);
                } catch (ParseException ignored) {
                    // Пробуем следующий формат
                }
            }
            throw new ParseException("Failed to parse date: " + dateString, 0);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}