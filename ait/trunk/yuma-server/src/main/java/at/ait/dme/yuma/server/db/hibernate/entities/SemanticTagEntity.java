package at.ait.dme.yuma.server.db.hibernate.entities;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.geo.GeoLocation;
import at.ait.dme.yuma.server.model.tag.PlainLiteral;
import at.ait.dme.yuma.server.model.tag.SemanticTag;

/**
 * A JPA database entity wrapper for a SemanticTag object.
 * 
 * @author Rainer Simon
 */
@Entity
@Table(name = "tags")
public class SemanticTagEntity implements Serializable {

	private static final long serialVersionUID = -7648256413169945758L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="annotations_id")
	private AnnotationEntity parent;
	
	@Column
	private String uri;

	@Column
	private String primaryLabel;

	@Column(length = 4096)		
	private String primaryDescription;
	
	@Column
	private String primaryLang;
	
	@Column
	private String type;
	
	@Column
	private SemanticRelationEntity relation;
	
	@OneToMany(mappedBy="parent",
			targetEntity=PlainLiteralEntity.class, 
			cascade=CascadeType.ALL)
	private List<PlainLiteralEntity> altLabels = new ArrayList<PlainLiteralEntity>();
	
	@OneToMany(mappedBy="parent",
			targetEntity=PlainLiteralEntity.class, 
			cascade=CascadeType.ALL)
	private List<PlainLiteralEntity> altDescriptions = new ArrayList<PlainLiteralEntity>();
	
	@Column
	private String geolocation;
	
	public SemanticTagEntity() { }
	
	public SemanticTagEntity(AnnotationEntity parent, SemanticTag t) {
		this.setParent(parent);
		this.setUri(t.getURI().toString());
		this.setPrimaryLabel(t.getPrimaryLabel());
		this.setPrimaryDescription(t.getPrimaryDescription());
		this.setPrimaryLang(t.getPrimaryLanguage());
		this.setType(t.getType());
		
		if (t.getRelation() != null)
			this.setRelation(new SemanticRelationEntity(t.getRelation()));
		
		for (PlainLiteral l : t.getAlternativeLabels()) {
			this.altLabels.add(new PlainLiteralEntity(this, l));
		}

		for (PlainLiteral l : t.getAlternativeDescriptions()) {
			this.altDescriptions.add(new PlainLiteralEntity(this, l));
		}
		
		if (t.getGeolocation() != null)
			this.setGeolocation(t.getGeolocation().toWKT());
	}
	
	public SemanticTag toSemanticTag()  {
		try {
			SemanticTag t = new SemanticTag(
				new URI(uri),
				primaryLabel,
				primaryDescription
			);
			
			t.setPrimaryLanguage(primaryLang);
			t.setType(type);
			if (relation != null)
				t.setSemanticRelation(relation.toSemanticRelation());
			
			for (PlainLiteralEntity l : altLabels) {
				t.addAlternativeLabel(l.toPlainLiteral());
			}
			
			for (PlainLiteralEntity l : altDescriptions) {
				t.addAlternativeDescription(l.toPlainLiteral());
			}
			
			if (geolocation != null)
				t.setGeolocation(GeoLocation.fromWKT(geolocation));
			
			return t;
		} catch (URISyntaxException e) {
			// Should never happen
			throw new RuntimeException("Malformed URI in annotation " + id + ": " + e.getMessage());
		} catch (InvalidAnnotationException e) {
			// Should never happen
			throw new RuntimeException("Malformed semantic relation in annotation " + id + ": " + e.getMessage());
		}
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getd() {
		return id;
	}
	
	public void setParent(AnnotationEntity parent) {
		this.parent = parent;
	}

	public AnnotationEntity getParent() {
		return parent;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setPrimaryLabel(String primaryLabel) {
		this.primaryLabel = primaryLabel;
	}

	public String getPrimaryLabel() {
		return primaryLabel;
	}

	public void setPrimaryDescription(String primaryDescription) {
		this.primaryDescription = primaryDescription;
	}

	public String getPrimaryDescription() {
		return primaryDescription;
	}

	public void setPrimaryLang(String primaryLang) {
		this.primaryLang = primaryLang;
	}

	public String getPrimaryLang() {
		return primaryLang;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setRelation(SemanticRelationEntity relation) {
		this.relation = relation;
	}

	public SemanticRelationEntity getRelation() {
		return relation;
	}

	public void setAltLabels(List<PlainLiteralEntity> altLabels) {
		this.altLabels = altLabels;
	}

	public List<PlainLiteralEntity> getAltLabels() {
		return altLabels;
	}

	public void setAltDescriptions(List<PlainLiteralEntity> altDescriptions) {
		this.altDescriptions = altDescriptions;
	}

	public List<PlainLiteralEntity> getAltDescriptions() {
		return altDescriptions;
	}

	public void setGeolocation(String geolocation) {
		this.geolocation = geolocation;
	}

	public String getGeolocation() {
		return geolocation;
	}

}
