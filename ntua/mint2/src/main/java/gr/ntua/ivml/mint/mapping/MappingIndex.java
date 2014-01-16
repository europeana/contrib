package gr.ntua.ivml.mint.mapping;

import net.sf.json.JSONObject;

/** 
 * @author Fotis Xenikoudakis
 * 
 * Class that describes an index to mappings within a mapping element.
 */
public class MappingIndex {
	/** Id of mapping element within a mapping object */
	private String id;
	/** Index of submapping withing a mapping case array. Use -1 if no mappings exists or if you don't care about the index. */
	private int index;
	/** Index of mapping cases array */
	private int caseIndex;
	/** Path to a specific subcondition. Only applies to conditions. */
	private String path;
	/** Key for a specific mapping case (i.e. structural) */
	private String key;
	
	public MappingIndex(String id) {
		this.setId(id);
		this.setIndex(-1);
		this.setCaseIndex(-1);
	}
	
	public MappingIndex(String id, int index) {
		this.setId(id);
		this.setIndex(index);
		this.setCaseIndex(-1);
	}
	
	public MappingIndex(String id, int index, int caseIndex) {
		this.setId(id);
		this.setIndex(index);
		this.setCaseIndex(caseIndex);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getCaseIndex() {
		return caseIndex;
	}
	
	public void setCaseIndex(int caseIndex) {
		this.caseIndex = caseIndex;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();

		if(this.id != null) object.put("id", this.id);
		if(this.path != null) object.put("path", this.path);
		if(this.key != null) object.put("key", this.key);
		object.put("index", this.index);
		object.put("case", this.caseIndex);
		
		return object;
	}
	
	public String toString() {
		return this.toJSON().toString(2);
	}
}
