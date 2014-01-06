package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class Condition extends JSONHandler {
	/** 
	 * Create a condition handler with an empty condition.
	 */
	public Condition() {
		super(new JSONObject());
	}
	
	/**
	 * Create a condition handler with contents of provided condition.
	 * @param condition condition json object.
	 */
	public Condition(JSONObject condition) {
		super(condition);
	}

	public static final String CONDITION_CLAUSES = "clauses";
	public static final String CONDITION_LOGICAL_OPERATOR = "logicalop";
	public static final String CONDITION_RELATIONAL_OPERATOR = "relationalop";

	/**
	 * Init this condition as a complex condition structure.
	 * @return true if a new condition is generated and model might need update.
	 */
	public boolean initComplexCondition() {
		String defaultLogicalOp = "AND";
		boolean conditionInit = false;
		
		if(object != null && !object.isEmpty()) {
			if(!this.has(CONDITION_LOGICAL_OPERATOR)) {
				object.put(CONDITION_LOGICAL_OPERATOR, defaultLogicalOp);
				JSONArray clauses = new JSONArray();
				JSONObject clause = new JSONObject();
				if(this.has(JSONHandler.MAPPING_XPATH) && this.getString(JSONHandler.MAPPING_XPATH).length() > 0) { clause.put(JSONHandler.MAPPING_XPATH, this.getString(JSONHandler.MAPPING_XPATH)); }
				if(this.has(JSONHandler.MAPPING_VALUE) && this.getString(JSONHandler.MAPPING_VALUE).length() > 0) { clause.put(JSONHandler.MAPPING_VALUE, this.getString(JSONHandler.MAPPING_VALUE)); }
				if(this.has(CONDITION_RELATIONAL_OPERATOR)) { clause.put(CONDITION_RELATIONAL_OPERATOR, this.getString("=")); }
				clauses.add(clause);
				object.put(CONDITION_CLAUSES, clauses);
			}
		} else {
			if(object == null) object = new JSONObject();
			object.put(CONDITION_LOGICAL_OPERATOR, defaultLogicalOp);
			object.put(CONDITION_CLAUSES, new JSONArray());
//			this.setCondition(index, object);
			conditionInit = true;
		}

		return conditionInit;
	}
	
	/**
	 * Set a clause key/value pair on this condition. 
	 * @param key. clause key to be set.
	 * @param value. value of clause key.
	 * @return self
	 */
	public void setClauseKey(String path, String key, String value) {
		if(!this.has(CONDITION_CLAUSES)) {
			this.initComplexCondition();
		}
		
		Condition.setClauseKey(this.object, path, key, value);
	}
	
	protected static void setClauseKey(JSONObject condition, String path, String key, String value) {
		//System.out.println("setting " + key + " on " + path + " of " + condition);
		if(path.length() == 0) {
			if(condition.containsKey(key)) { condition.remove(key); }
			condition.put(key, value);
		} else {
			JSONArray clauses = (JSONArray) condition.get(CONDITION_CLAUSES);
			if(clauses.isEmpty()) {
				clauses.add(new JSONObject());
			}
			if(path.contains(".")) {
				String[] parts = path.split("\\.", 2);
				int index = Integer.parseInt(parts[0]);
				setClauseKey((JSONObject) clauses.get(index), parts[1], key, value);
			} else {
				int index = Integer.parseInt(path);
				setClauseKey((JSONObject) clauses.get(index), "", key, value);
			}
		}
	}
	
	/**
	 * Remove a clause key from this condition.
	 * @param index. Set index caseId and path to access the clause.
	 * @param key. clause key to be removed.
	 * @return self
	 */
	public void removeClauseKey(String path, String key) {
		if(!this.has(CONDITION_CLAUSES)) return;
		Condition.removeClauseKey(this.object, path, key);
	}
	
	protected static void removeClauseKey(JSONObject condition, String path, String key) {
		if(path.length() == 0) {
			condition.remove(key);
		} else {
			if(condition.containsKey(CONDITION_CLAUSES)) {
				JSONArray clauses = (JSONArray) condition.get(CONDITION_CLAUSES);
				if(path.contains(".")) {
					String[] parts = path.split("\\.", 2);
					int index = Integer.parseInt(parts[0]);
					removeClauseKey((JSONObject) clauses.get(index), parts[1], key);
				} else {
					int index = Integer.parseInt(path);
					removeClauseKey((JSONObject) clauses.get(index), "", key);
				}
			}			
		}
	}
	
	/**
	 * Add a condition clause.
	 * @param path. Path of the clause.
	 * @param complex. True if clause is complex (ie. has subclauses joined by logical operator)
	 * @return self
	 */
	public void addClause(String path, boolean complex) {
		if(!this.has(CONDITION_CLAUSES)) {
			this.initComplexCondition();
		}
		
		Condition.addClause(this.object, path, complex);
	}
	
	protected static void addClause(JSONObject condition, String path, boolean complex) {
		if(condition.containsKey(CONDITION_CLAUSES)) {
			Condition.addClause((JSONArray) condition.get(CONDITION_CLAUSES), path, complex);
		}
	}
	
	protected static void addClause(JSONArray clauses, String path, boolean complex) {
		JSONObject clause = new JSONObject();
		
		if(complex) {
			clause.put(CONDITION_LOGICAL_OPERATOR, "AND");
			JSONArray array = new JSONArray();
			array.add(new JSONObject());
			clause.put(CONDITION_CLAUSES, array);
		}

		if(path.endsWith(".")) path = path.substring(0, path.length()-1);
		//log.debug("addConditionClause at " + path);
		if(path.contains(".")) {
				String[] parts = path.split("\\.", 2);
				//System.out.println("'" + path + "' '" + parts[0] + "' '" + parts[1] + "'");
				int index = Integer.parseInt(parts[0]);
				Condition.addClause((JSONObject) clauses.get(index), parts[1], complex);
		} else {
			try {
				int index = Integer.parseInt(path);
				clauses.add(index+1, clause);
			} catch(Exception e) {
			}
		}
	}

	
	/**
	 * Remove a condition clause.
	 * @param path. Path of the clause.
	 * @return self
	 */
	public void removeClause(String path) {
		if(!this.has(CONDITION_CLAUSES));
		Condition.removeClause(this.object, path);
	}
	
	protected static void removeClause(JSONObject condition, String path) {
		if(condition.containsKey(CONDITION_CLAUSES)) {
			Condition.removeClause((JSONArray) condition.get(CONDITION_CLAUSES), path);
		}
	}
	
	protected static void removeClause(JSONArray clauses, String path) {
		if(path.length() > 0) {
			if(path.contains(".")) {
				String[] parts = path.split("\\.", 2);
				int index = Integer.parseInt(parts[0]);
				if(parts[1].length() > 0) {
					Condition.removeClause((JSONArray) clauses.get(index), parts[1]);
				} else {
					clauses.remove(index);
				}
			} else {
				int index = Integer.parseInt(path);
				clauses.remove(index);
			}
		}
	}
}