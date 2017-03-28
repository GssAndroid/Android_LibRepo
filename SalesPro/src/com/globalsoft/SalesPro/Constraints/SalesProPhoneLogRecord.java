package com.globalsoft.SalesPro.Constraints;

import java.io.Serializable;
import java.util.Date;

public class SalesProPhoneLogRecord implements Serializable{
	
	public int aplr_dataId;
	public String aplr_number;
	public String aplr_name;
	public Date aplr_time;
	public int aplr_type;
	public int aplr_duration;
	public int aplr_id;			//the id in the database
	
	public SalesProPhoneLogRecord(String _number, String _name, Date _time, int _type, int _duration, int _id, int _dataId) {
		aplr_number = _number;
		aplr_name = _name;
		aplr_time = _time;
		aplr_type = _type;
		aplr_duration = _duration;
		aplr_id = _id;
		aplr_dataId = _dataId;
	}
	
	public SalesProPhoneLogRecord() {
		aplr_number = "";
		aplr_name = "";
		aplr_time = null;
		aplr_type = 0;
		aplr_duration = 0;
		aplr_id = 0;
		aplr_dataId = 0;
	}

	public boolean isSameContact(SalesProPhoneLogRecord _a) {
		return (this.aplr_number.compareTo(_a.aplr_number)==0);
	}
	
}//End of class SalesProPhoneLogRecord