package org.match.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.match.models.EntrySpectateurDto;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Reader personnalisé pour lire un tableau JSON
 */


public class JsonArrayItemReader implements ItemReader<EntrySpectateurDto> {

    private Iterator<EntrySpectateurDto> iterator;
    private final String resourcePath;

    public JsonArrayItemReader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public EntrySpectateurDto read() throws Exception {
        if (iterator == null) {
            iterator = loadJsonArray();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

    private Iterator<EntrySpectateurDto> loadJsonArray() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ClassPathResource resource = new ClassPathResource(resourcePath);

        // Lecture du tableau JSON complet
        EntrySpectateurDto[] array = mapper.readValue(
                resource.getInputStream(),
                EntrySpectateurDto[].class
        );

        // Conversion en liste
        List<EntrySpectateurDto> items = new ArrayList<>();
        for (EntrySpectateurDto item : array) {
            items.add(item);
        }

        System.out.println("✅ " + items.size() + " spectateurs chargés depuis le JSON");

        return items.iterator();
    }
}