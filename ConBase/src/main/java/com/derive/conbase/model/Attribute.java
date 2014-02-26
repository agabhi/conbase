package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Attribute {

	private static String SEPARATOR = "{::}";
	private Long id;
	private String name;
	private Short type;
	private List<String> options;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	@JsonIgnore
	public String getValue() {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOptionsFromValue(String value) {
		List<String> options = null;
		if (StringUtils.isNotBlank(value)) {
			String[] values = value.trim().split("\\{::\\}");
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

	public Map<Boolean, List<String>> validate() {
		Map<Boolean, List<String>> resultMap = new LinkedHashMap<Boolean, List<String>>();
		List<String> messages = new ArrayList<String>();
		boolean status = true;
		if (StringUtils.isBlank(name)) {
			status = false;
			messages.add("Name cannot be empty");
		}
		if (CollectionUtils.isNotEmpty(options)) {
			boolean nonEmptyOptionFound = false;
			for (String option : options) {
				if (StringUtils.isNotBlank(option)) {
					nonEmptyOptionFound = true;
				}
			}
			if (!nonEmptyOptionFound) {
				messages.add("At least one non-empty option should be provided.");
			}
		} else {
			messages.add("At least one non-empty option should be provided.");
		}
		resultMap.put(status, messages);
		return resultMap;
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
		Attribute other = (Attribute) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
