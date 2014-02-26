package com.derive.conbase.model;

import java.util.List;

public class BarchartData {
	private List<LayerChartEntry> layerChartEntries;
	private List<Record> records;
	private List<RecordAttribute> recordAttributes;
	
	public List<LayerChartEntry> getLayerChartEntries() {
		return layerChartEntries;
	}
	public void setLayerChartEntries(List<LayerChartEntry> layerChartEntries) {
		this.layerChartEntries = layerChartEntries;
	}
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}
	public List<RecordAttribute> getRecordAttributes() {
		return recordAttributes;
	}
	public void setRecordAttributes(List<RecordAttribute> recordAttributes) {
		this.recordAttributes = recordAttributes;
	}
}
