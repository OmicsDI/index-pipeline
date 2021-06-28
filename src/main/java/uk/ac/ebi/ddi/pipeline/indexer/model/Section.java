package uk.ac.ebi.ddi.pipeline.indexer.model;

import java.util.List;

public class Section {

    List<Attributes> attributes;
    Subsections subsections;
    Links links;

    public List<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        this.attributes = attributes;
    }

    public Subsections getSubsections() {
        return subsections;
    }

    public void setSubsections(Subsections subsections) {
        this.subsections = subsections;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
