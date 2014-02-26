package com.derive.conbase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.RecordTypeDAO;
import com.derive.conbase.dao.SerialDAO;
import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.RecordType;
import com.derive.conbase.model.RecordTypeAttribute;

@Service
public class RecordTypeService {
	
	@Autowired
	RecordTypeDAO recordTypeDAO;
	
	@Autowired
	SerialDAO serialDAO;
	
	public JqGridData<RecordType> getRecordTypesByPage(int pageNo, int size) {
		return recordTypeDAO.findRecordTypesByPage(pageNo, size);
	}
	
	public RecordType getRecordTypeByName(String name) {
		return recordTypeDAO.findRecordTypeByName(name);
	}
	
	public void addRecordType(RecordType recordType) {
		recordTypeDAO.save(recordType);
	}
	
	public RecordType getRecordTypeWithAttributesById(long recordTypeId) {
		RecordType recordType = recordTypeDAO.findRecordTypeById(recordTypeId);
		recordType.setCustomAttributes(recordTypeDAO.findCustomAttributesByRecordTypeId(recordTypeId));
		recordType.setLayerAttributes(recordTypeDAO.findLayerAttributesByRecordTypeId(recordTypeId));
		if (recordType.getSerial() != null && recordType.getSerial().getId() != null) {
			recordType.setSerial(serialDAO.findSerialById(recordType.getSerial().getId()));
		}
		return recordType;
	}
	
	public RecordTypeAttribute getRecordTypeAttributeById(long recordTypeAttributeId) {
		RecordTypeAttribute recordTypeAttribute = recordTypeDAO.findRecordTypeAttributeById(recordTypeAttributeId);
		return recordTypeAttribute;
	}
	
	public void updateRecordType(RecordType recordType) {
		recordTypeDAO.updateRecordType(recordType);
	}
}
