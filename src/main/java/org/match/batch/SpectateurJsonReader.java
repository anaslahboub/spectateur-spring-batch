package org.match.batch;

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
@Configuration
public class SpectateurJsonReader  {

    /**
     * The @StepScope annotation ensures that the ItemReader bean is instantiated and managed within the scope of a step. This allows for dynamic parameters or configurations to be injected into the reader.
     * JsonItemReaderBuilder: Configures the reader to read Spectateur objects from the spectateur.json file using Jackson for JSON parsing.
     * @return SpectateurDto
     */
    @Bean
    @StepScope
    public ItemReader<EntrySpectateurDto> jsonItemReader() {
        return new JsonArrayItemReader("input/spectateurs.json");
    }

//    @Bean
//    @StepScope
//    public JsonItemReader<EntrySpectateurDto> jsonItemReader() {
//
////        JsonMapper jsonMapper = JsonMapper.builder()
////                .findAndAddModules() // gère LocalDate, LocalDateTime…  // Important : validation stricte
////                .build();
////        JacksonJsonObjectReader<EntrySpectateurDto> jsonObjectReader = new JacksonJsonObjectReader<>(EntrySpectateurDto.class);
////
////        jsonObjectReader.setMapper(jsonMapper);
////
////        return new JsonItemReaderBuilder<EntrySpectateurDto>()
////                .name("SpectateurJsonReader")
////                .resource(new ClassPathResource("input/spectateurs.json.json"))
////                .jsonObjectReader(jsonObjectReader)
////                .build();
//
//        /**
//         * Bean pour lire un tableau JSON
//         * Utilise le JsonArrayItemReader personnalisé
//         * @return ItemReader pour EntrySpectateurDto
//         */
//
//    }


    /**
     * Sans @StepScope, ton reader est créé comme un bean singleton au démarrage de l’application.
     * Il ne peut donc pas recevoir des JobParameters ou des données dynamiques spécifiques à l’exécution du Step.
     * Cela limite la flexibilité et peut provoquer des erreurs si tu veux passer un fichier ou une valeur dynamique au job.
     * Avec @StepScope, ton reader est instancié juste avant l’exécution du Step, et peut recevoir les paramètres dynamiques.
     */


}
