package at.ait.dme.yuma.server.db.hibernate.entities;

import javax.persistence.Embeddable;

import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.tag.SemanticRelation;

/**
 * A JPA database entity wrapper for a SemanticRelation object.
 * 
 * @author Rainer Simon
 */
@Embeddable
public class SemanticRelationEntity {
	
	private String namespace;
	
	private String property;
	
	public SemanticRelationEntity() { }
	
	public SemanticRelationEntity(SemanticRelation relation) {
		this.setNamespace(relation.getNamespace());
		this.setProperty(relation.getProperty());
	}
	
	public SemanticRelation toSemanticRelation() throws InvalidAnnotationException {
		return new SemanticRelation(namespace, property);
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
