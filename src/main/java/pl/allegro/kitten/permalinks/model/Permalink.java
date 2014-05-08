package pl.allegro.kitten.permalinks.model;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@ApiModel(value = "Source URL redirect to Destination URL")
@XmlRootElement(name = "Permalink")
public class Permalink {

    @XmlElement(name = "sourceUrl")
    @ApiModelProperty(value = "Source URL", required = true)
    private String sourceUrl;

    @XmlElement(name = "destinationUrl")
    @ApiModelProperty(value = "Destination URL", required = true)
    private String destinationUrl;

    public Permalink() {
    }

    public Permalink(String sourceUrl, String destinationUrl) {
        this.sourceUrl = sourceUrl;
        this.destinationUrl = destinationUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }
}
