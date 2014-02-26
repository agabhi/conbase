package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


public class RecordTypeAttribute {
	
	private static String SEPARATOR = "{::}";
	private Long id;
	private String name;
	private int type;
	
	private List<String> options;
	private HashMap<String,Integer> validations;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getOptionsString() {
		if (CollectionUtils.isNotEmpty(options)) {
			StringBuffer buffer = new StringBuffer();
			for (String option : options) {
				if (buffer.length() > 0) {
					buffer.append(SEPARATOR);
				}
				buffer.append(option);
			}
			return buffer.toString();
		}
		return null;
	}
	
	public List<String> getOptions() {
		return options;
	}
	public void setOptions(List<String> options) {
		this.options = options;
	}
	public HashMap<String, Integer> getValidations() {
		return validations;
	}
	public void setValidations(HashMap<String, Integer> validations) {
		this.validations = validations;
	}
	
	public void setOptionsFromOptionsString(String optionsString) {
		List<String> options = null;
		if (StringUtils.isNotBlank(optionsString)) {
			String[] values =  optionsString.trim().split("\\{::\\}");
			if (values != null && values.length > 0) {
				for (String valuee : values) {
					if (valuee != null && valuee.trim().length() > 0) {
						if (options == null) {
							options = new ArrayList<String>();
						}
						options.add(valuee);
					}
				}
			}
		}
		this.options = options;
	}
	
	public String getValidationsString() {
		String validationsString = null;
		if (MapUtils.isNotEmpty(validations)) {
			for (Map.Entry<String, Integer> entry : validations.entrySet()) {
				String validationKey = entry.getKey();
				Integer value = entry.getValue();
				if (validationKey != null && validationKey.trim().length() > 0 && value != null) {
					if (validationsString != null) {
						validationsString += "{::::}" + validationKey + "{::}" + value;
					} else {
						validationsString = "" + validationKey + "{::}" + value;
					}
				}
			}
		}
		return validationsString;
	}
	
	public void setValidationsFromValidationsString(String validationsString) {
		HashMap<String,Integer> validations = null;
		if (StringUtils.isNotBlank(validationsString)) {
			String[] values =  validationsString.trim().split("\\{::::\\}");
			if (values != null && values.length > 0) {
				for (String valuee : values) {
					if (valuee != null && valuee.trim().length() > 0) {
						String[] valuees = valuee.split("\\{::\\}");
						if (valuees != null && valuees.length == 2) {
							if (validations == null) {
								validations = new LinkedHashMap<String, Integer>();
							}
							validations.put(valuees[0], Integer.valueOf(valuees[1]));
						}
					}
				}
			}
		}
		this.validations = validations;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordTypeAttribute other = (RecordTypeAttribute) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
