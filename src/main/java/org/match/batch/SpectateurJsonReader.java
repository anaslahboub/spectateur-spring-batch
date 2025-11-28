package org.match.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.match.models.EntrySpectateurDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectReader;
import org.springframework.batch.infrastructure.item.json.JsonItemReader;
import org.springframework.batch.infrastructure.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configuration
@Slf4j
public class SpectateurJsonReader implements ItemReader<EntrySpectateurDto>  {

    /**
     * The @StepScope annotation ensures that the ItemReader bean is instantiated and managed within the scope of a step. This allows for dynamic parameters or configurations to be injected into the reader.
     * JsonItemReaderBuilder: Configures the reader to read Spectateur objects from the spectateur.json file using Jackson for JSON parsing.
     * @return SpectateurDto
     */
    private Iterator<EntrySpectateurDto> iterator;



    @Override
    public EntrySpectateurDto read() throws Exception {
        if (iterator == null) {
            iterator = loadJsonArray();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

    private Iterator<EntrySpectateurDto> loadJsonArray() throws IOException {
        log.info("📖 Chargement du fichier JSON: input/spectateurs.json");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ClassPathResource resource = new ClassPathResource("input/spectateurs.json");
        EntrySpectateurDto[] array = mapper.readValue(resource.getInputStream(), EntrySpectateurDto[].class);

        List<EntrySpectateurDto> items = new ArrayList<>();
        for (EntrySpectateurDto item : array) {
            items.add(item);
        }

        log.info("✅ {} spectateurs chargés depuis le JSON", items.size());

        return items.iterator();
    }

    /**
     * Sans @StepScope, ton reader est créé comme un bean singleton au démarrage de l’application.
     * Il ne peut donc pas recevoir des JobParameters ou des données dynamiques spécifiques à l’exécution du Step.
     * Cela limite la flexibilité et peut provoquer des erreurs si tu veux passer un fichier ou une valeur dynamique au job.
     * Avec @StepScope, ton reader est instancié juste avant l’exécution du Step, et peut recevoir les paramètres dynamiques.
     */


}
