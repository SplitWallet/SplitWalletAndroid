package com.example.splitwallet;

import com.example.splitwallet.models.LocalDateAdapter;
import com.google.gson.JsonPrimitive;
import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

public class LocalDateAdapterTest {

    private final LocalDateAdapter adapter = new LocalDateAdapter();

    @Test
    public void testSerialize() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        JsonPrimitive json = (JsonPrimitive) adapter.serialize(date, null, null);
        assertEquals("2023-12-25", json.getAsString());
    }

    @Test
    public void testDeserialize() {
        JsonPrimitive json = new JsonPrimitive("2023-12-25");
        LocalDate date = adapter.deserialize(json, null, null);
        assertEquals(LocalDate.of(2023, 12, 25), date);
    }
}

