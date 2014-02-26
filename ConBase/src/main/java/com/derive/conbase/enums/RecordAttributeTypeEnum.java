package com.derive.conbase.enums;

public enum RecordAttributeTypeEnum {
	OPTION(1), STRING(2), TEXT(3), INTEGER(4), DECIMAL(5), FROM_LENGTH(6), TO_LENGTH(7), LEVEL(8), LAYER(9), STRUCTURE(10), STRUCTURE_ITEM(11), SERIAL(12) ;
	
	private int value;
	 
	private RecordAttributeTypeEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
