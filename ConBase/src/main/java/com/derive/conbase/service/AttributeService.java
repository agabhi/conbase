package com.derive.conbase.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.AttributeDAO;
import com.derive.conbase.model.Attribute;

@Service
public class AttributeService {
	
	@Autowired
	AttributeDAO attributeDAO;
	
	public List<Attribute> getAttributes() {
		return attributeDAO.findAttributes();
	}
	
	public List<Attribute> getAttributesByType(Short type) {
		return attributeDAO.findAttributesByType(type);
	}
	
	public Attribute getAttributeByName(String name) {
		return attributeDAO.findAttributeByName(name);
	}
	
	public void addAttribute(Attribute attribute) {
		attributeDAO.save(attribute);
	}
	
	public void updateAttribute(Attribute attribute) {
		attributeDAO.update(attribute);
	}
	
	public void deleteAttribute(Long attributeId) {
		attributeDAO.delete(attributeId);
	}
}
