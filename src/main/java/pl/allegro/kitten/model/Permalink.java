package pl.allegro.kitten.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Permalink {

    private String sourceUrl;
    private String destinationUrl;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }
}
