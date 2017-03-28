package com.globalsoft.ProductLib.Database;

import java.util.ArrayList;

import android.content.Context;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductDBConstants {
	
	//Database Related Constant
	public static String PROD_LIST_TABLE_CREATE = "";
	public static String[] productMetaArrayString;	
	
	public static String PROD_ATTA01_LIST_TABLE_CREATE = "";
	public static String[] productATTA01MetaArrayString;	
	
	public static String PROD_ATTA01R_LIST_TABLE_CREATE = "";
	public static String[] productATTA01RMetaArrayString;	
	
	public static String PROD_UICONF_LIST_TABLE_CREATE = "";
	public static String[] productUICONFMetaArrayString;	

	public static String PROD_SRCCAT_LIST_TABLE_CREATE = "";
	public static String[] productSRCCATMetaArrayString;
	
	public static void setTableName_ColumnName(Context ctx, String tableName){
		String columnLists = "";
		ProductColumnDB dbObjColumns = null;
		ArrayList metaListArray = new ArrayList();	
		try {
			if(tableName != null && tableName.length() > 0){
				DBConstants.DB_TABLE_NAME = tableName;
			}
			if(dbObjColumns == null)
				dbObjColumns = new ProductColumnDB(ctx);			
			columnLists = dbObjColumns.readColumnNames(DBConstants.DB_TABLE_NAME);
			SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);				
			if(columnLists != null && columnLists.length() > 0){
				SapGenConstants.showLog("columnLists: "+columnLists);				
				if(metaListArray != null && metaListArray.size() > 0){
					metaListArray.clear();
				}				
				String[] separated = columnLists.split(":");
				if(separated != null && separated.length > 0){
					for(int i=0; i<separated.length; i++){
						SapGenConstants.showLog("  "+separated[i]);
						metaListArray.add(separated[i].toString().trim());
					}
				}
				DataBasePersistent.setColumnName(metaListArray);	
			}else{
				SapGenConstants.showLog("No Data Available for product list meta data!");
			}
			dbObjColumns.closeDBHelper();
		} catch (Exception sgghr) {
			SapGenConstants.showErrorLog("On setTableName_ColumnName : "+sgghr.toString());
		}
	}//fn setTableName_ColumnName
	
}//end of PriceListDBConstants