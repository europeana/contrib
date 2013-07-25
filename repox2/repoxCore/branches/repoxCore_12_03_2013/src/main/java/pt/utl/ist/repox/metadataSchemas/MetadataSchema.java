package pt.utl.ist.repox.metadataSchemas;

import java.util.Date;
import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 14-06-2012
 * Time: 16:58
 */
public class MetadataSchema {

    private String designation;
    private String shortDesignation;
    private String description;
//    private Date creationDate;
    private String namespace;
    private String notes;

    private List<MetadataSchemaVersion> metadataSchemaVersions;

    public MetadataSchema(String designation, String shortDesignation, String description, String namespace,
                          String notes, List<MetadataSchemaVersion> metadataSchemaVersions) {
        this.designation = designation;
        this.shortDesignation = shortDesignation;
        this.description = description;
//        this.creationDate = creationDate;
        this.namespace = namespace;
        this.notes = notes;
        this.metadataSchemaVersions = metadataSchemaVersions;
    }

    public String getDesignation() {
        return designation;
    }

    public String getShortDesignation() {
        return shortDesignation;
    }

    public String getDescription() {
        return description;
    }

//    public Date getCreationDate() {
//        return creationDate;
//    }

    public String getNamespace() {
        return namespace;
    }

    public String getNotes() {
        return notes;
    }

    public List<MetadataSchemaVersion> getMetadataSchemaVersions() {
        return metadataSchemaVersions;
    }
}
