package com.derive.conbase.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.SerialDAO;
import com.derive.conbase.model.Serial;

@Service
public class SerialService {
	
	@Autowired
	SerialDAO serialDAO;
	
	
	public List<Serial> getSerials() {
		return serialDAO.findSerials();
	}
	
	public void addSerial(Serial serial) {
		serialDAO.save(serial);
	}
	
	public Serial getSerialByPrefix(String prefix) {
		return serialDAO.findSerialByPrefix(prefix);
	}
	
	public Serial getSerialByName(String name) {
		return serialDAO.findSerialByName(name);
	}
}
