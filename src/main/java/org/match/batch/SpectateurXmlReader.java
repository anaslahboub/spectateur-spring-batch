package org.match.batch;

import lombok.extern.slf4j.Slf4j;
import org.match.models.EntrySpectateurDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.batch.infrastructure.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
@Slf4j
@Configuration
public class SpectateurXmlReader {


    /**
     *The above ReaderConfig class configures the StaxEventItemReader for reading XML files. It uses Jaxb2Marshaller to unmarshal XML data into Spectateur objects.
     *  Here is an explanation of what the reader method does:
     * Jaxb2Marshaller: This is configured with the Employee class to handle the unmarshalling process.
     * StaxEventItemReaderBuilder: This builds the StaxEventItemReader with the specified name, resource (spectateurs XML file), root element (spectateur), and the configured unmarshaller.
     * @return SpectateurDto
     */
    @Bean
    @StepScope
    public StaxEventItemReader<EntrySpectateurDto> reader() {
        log.info("📖 Configuration du reader XML pour spectateurs");

        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(EntrySpectateurDto.class);

        return new StaxEventItemReaderBuilder<EntrySpectateurDto>()
                .name("SpectateurXmlReader")
                .resource(new ClassPathResource("input/spectateurs.xml"))
                .addFragmentRootElements("spectatorEntry")
                .unmarshaller(unmarshaller)
                .build();
    }
}
