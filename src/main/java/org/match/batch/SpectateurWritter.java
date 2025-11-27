package org.match.batch;

import jakarta.persistence.EntityManagerFactory;
import org.match.models.EntrySpectateur;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpectateurWritter  {

    @Bean
    public JpaItemWriter<EntrySpectateur> writer(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<EntrySpectateur>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(false)
                .build();
    }

}
