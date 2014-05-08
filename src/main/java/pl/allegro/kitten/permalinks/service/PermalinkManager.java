package pl.allegro.kitten.permalinks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.kitten.permalinks.model.Permalink;
import pl.allegro.kitten.permalinks.repository.PermalinkRepository;

@Service
public class PermalinkManager {

    @Autowired
    private PermalinkRepository permalinkRepository;

    public PermalinkManager() {}

    public PermalinkManager(PermalinkRepository permalinkRepository) {
        this.permalinkRepository = permalinkRepository;
    }

    public Permalink get(String sourceUrl) {
        String destinationUrl = permalinkRepository.findDestinationUrlBySourceUrl(sourceUrl);

        if (null == destinationUrl) {
            return null;
        }

        return new Permalink(sourceUrl, destinationUrl);
    }

    public void set(Permalink permalink) throws PermalinkInvalidException {
        if (null == permalink.getSourceUrl() || null == permalink.getDestinationUrl()) {
            throw new PermalinkInvalidException("Permalink is not valid.");
        }

        permalinkRepository.save(permalink.getSourceUrl(), permalink.getDestinationUrl());
    }

    public void delete(Permalink permalink) {
        permalinkRepository.deleteBySourceUrl(permalink.getSourceUrl());
    }
}
