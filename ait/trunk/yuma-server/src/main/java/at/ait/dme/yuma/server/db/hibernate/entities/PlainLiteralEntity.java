package at.ait.dme.yuma.server.db.hibernate.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import at.ait.dme.yuma.server.model.tag.PlainLiteral;

@Entity
@Table(name = "plainliterals")
public class PlainLiteralEntity implements Serializable {

	private static final long serialVersionUID = -1666936125696909441L;

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="tags_id")
	private SemanticTagEntity parent;
	
	@Column
	private String value;
	
	@Column
	private String lang;
	
	public PlainLiteralEntity() { }
	
	public PlainLiteralEntity(SemanticTagEntity parent, PlainLiteral literal) {
		this.setParent(parent);
		this.setValue(literal.getValue());
		this.setLang(literal.getLanguage());
	}
	
	public PlainLiteral toPlainLiteral() {
		return new PlainLiteral(value, lang);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setParent(SemanticTagEntity parent) {
		this.parent = parent;
	}

	public SemanticTagEntity getParent() {
		return parent;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLang() {
		return lang;
	}

}
