package com.derive.conbase.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.RecordDAO;
import com.derive.conbase.model.Record;
import com.derive.conbase.model.RecordAttribute;
import com.derive.conbase.model.RecordSearchCriteria;

@Service
public class RecordService {
	
	@Autowired
	RecordDAO recordDAO;
	
	public void addRecord(Record record) {
		recordDAO.addRecord(record);
	}
	
	public void updateRecord(Record record) {
		recordDAO.updateRecord(record);
	}
	
	public Record getRecordById(long recordId) {
		return recordDAO.findRecordById(recordId);
	}
	
	public Long getRecordsCount(long recordTypeId, RecordSearchCriteria search) {
		return recordDAO.findRecordsCount(recordTypeId, search);
	}
	public List<Record> getRecordsByPage(long recordTypeId, int pageNo, int size, RecordSearchCriteria search) {
		return recordDAO.findRecordsByPage(recordTypeId, pageNo, size, search);
	}
	
	public List<RecordAttribute> getRecordAttributesByRecordIds(List<Long> recordIds) {
		return recordDAO.findRecordAttributesByRecordIds(recordIds);
	}
	
	public List<RecordAttribute> getRecordAttributesByRecordIdsByRecordTypeAttributeId(List<Long> recordIds, Long recordTypeAttributeId) {
		return recordDAO.findRecordAttributesByRecordIdsByRecordTypeAttributeId(recordIds, recordTypeAttributeId);
	}
	
	public List<Record> getRecordsByRecordTypeIdByConfigIdsByFromByTo(Long recordTypeId, List<Long> layerAttributeConfigIds, int from, int to) {
		return recordDAO.findRecordsByRecordTypeIdByConfigIdsByFromByTo(recordTypeId, layerAttributeConfigIds, from, to);
	}
}
