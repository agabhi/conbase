package com.derive.conbase.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.StructureDAO;
import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.Structure;

@Service
public class StructureService {
	
	@Autowired
	StructureDAO structureDAO;
	
	public List<Structure> getActiveStructures() {
		return structureDAO.findActiveStructures();
	}
	
	public JqGridData<Structure> getStructuresByPage(int pageNo, int size) {
		return structureDAO.findStructuresByPage(pageNo, size);
	}
	
	public Structure getStructureById(long id) {
		return structureDAO.findStructureById(id);
	}
	
	public Structure getActiveStructureByName(String name) {
		return structureDAO.findActiveStructureByName(name);
	}
	
	public void addStructure(Structure structure) {
		structureDAO.save(structure);
	}
	
	public void updateStructure(Structure structure) {
		structureDAO.updateStructure(structure);
	}
	
	public void deactivate(Long structureId) {
		structureDAO.deactivate(structureId);
	}

}
