package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class LayerAttribute {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
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
		LayerAttribute other = (LayerAttribute) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		return true;
	}

	private static String SEPARATOR = "{::}";
	
	private Long id;
	private Attribute attribute;
	private boolean mandatory;
	private String allowedValues;
	private List<String> options;
	public Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public List<String> getOptions() {
		if (allowedValues != null && allowedValues.equals("All")) {
			return attribute.getOptions();
		}
		return options;
	}
	
	public void setOptions(List<String> options) {
		this.options = options;
	}
	public String getAllowedValues() {
		return allowedValues;
	}
	public String getOptionsString() {
		if (allowedValues != null && allowedValues.equals("All")) {
			return null;
		} else {
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
		}
		return null;
	}
	
	public void setOptionsFromOptionsString(String optionsString) {
		List<String> options = null;
		if (StringUtils.isNotBlank(optionsString)) {
			String[] values = optionsString.trim().split("\\{::\\}");
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
	
	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
	}
	
	public Map<Boolean, List<String>> validate() {
		Map<Boolean, List<String>> resultMap = new LinkedHashMap<Boolean, List<String>>();
		List<String> messages = new ArrayList<String>();
		boolean status = true;
		if (!allowedValues.equals("All")) {
			if (CollectionUtils.isNotEmpty(options)) {
				boolean nonEmptyOptionFound = false;
				for (String option : options) {
					if (StringUtils.isNotBlank(option)) {
						nonEmptyOptionFound = true;
					}
				}
				if (!nonEmptyOptionFound) {
					messages.add("At least one option should be selected.");
				}
			} else {
				messages.add("At least option should be selected.");
			}
		}
		resultMap.put(status, messages);
		return resultMap;
	}
	
	
}
