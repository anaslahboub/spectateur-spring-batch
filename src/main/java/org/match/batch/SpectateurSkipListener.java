package org.match.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpectateurSkipListener implements SkipListener<Object, Object> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("Erreur lors de la lecture", t);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        log.error("Erreur lors du traitement de l'élément: {}", item, t);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        log.error("Erreur lors de l'écriture de l'élément: {}", item, t);
    }
}
