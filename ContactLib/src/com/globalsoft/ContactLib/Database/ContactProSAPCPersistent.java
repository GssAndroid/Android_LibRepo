package com.globalsoft.ContactLib.Database;

import java.util.ArrayList;

import com.globalsoft.ContactLib.Utils.ContactsConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactProSAPCPersistent {

	//Database Related Constants
    public static final String KEY_ROWID = "id";
    public static final String KEY_CONTACTDEVID = "cdevid";
    public static final String KEY_CONTACTSAPID = "csapid";
    public static final String KEY_CONTACTSAPCUSID = "csapcuid";    
    public static final String KEY_CONTACTSAPCUSFNAME = "csapcufname";
    public static final String KEY_CONTACTSAPCUSLNAME = "csapculname";
    //public static final String KEY_CONTACTFLAG = "contactflag";
    
    private static final String DATABASE_NAME = "CPSAPContactsDB";
    private static final String DATABASE_TABLE = "contactdetails";
    private static final int DATABASE_VERSION = 1;

    Cursor c;
    long id;

    private static final String DATABASE_CREATE =
        "create table "+DATABASE_TABLE+" (id integer primary key autoincrement, "  
   	 	 + KEY_CONTACTDEVID+" text not null,"     
         + KEY_CONTACTSAPID+" text not null,"   
         + KEY_CONTACTSAPCUSID+" text not null,"
         + KEY_CONTACTSAPCUSFNAME+" text not null,"
         + KEY_CONTACTSAPCUSLNAME+" text not null);";
         //+ KEY_CONTACTFLAG+" text not null);";

    private  final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase sqlitedatabase;
    
    public ContactProSAPCPersistent(Context ctext){
    	context = ctext;    
    	DBHelper = new DatabaseHelper(context);
		sqlitedatabase = DBHelper.getWritableDatabase();
		DBHelper.close();
		sqlitedatabase.close();
    }
    
    public void insertContactDetails(String cdevid, String csapid, String csapcuid, String csapcufname, 
    		String csapculname){
    	try {
			boolean isuserexists = false;
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
		    
		    if(isuserexists == false)	
		    	insertRow(cdevid, csapid, csapcuid, csapcufname, csapculname);
		    
			sqlitedatabase.close();
			
		} catch (Exception e) {
			System.out.println("Error from user details insterting->"+e.toString());
		}
    }//fn insertContactDetails
    
    public void closeDBHelper(){
    	DBHelper.close();
    	if (c != null) {
            c.close();
            c = null;
    	}
    }//fn closeDBHelper       
    
    public boolean checkContactExists(String idno){
    	boolean idexists = false;
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_CONTACTDEVID+"="+idno, null);

			if (c.moveToFirst())
			{
			     do {
			    	 int index = c.getColumnIndex(KEY_CONTACTDEVID);
		     		 String id_no = c.getString(index);
		     		 if(idno.equalsIgnoreCase(id_no)){
		     			 idexists = true;
     				 	 sqlitedatabase.close();
     				 	 return idexists;
		     		 }
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			System.out.println("Error from user details checking->"+e.toString());
		}   
    	return idexists;
    }//fn checkContactExists
    
    public void getContactDetails(String localContactId){
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_CONTACTDEVID+"="+localContactId, null);

			if (c.moveToFirst())
			{
			     do {
			    	 int index = c.getColumnIndex(KEY_CONTACTDEVID);
		     		 String dev_no = c.getString(index);
		     		 if(localContactId.equalsIgnoreCase(dev_no)){
		     			ContactsConstants.CONTACTSAPID = c.getString(2);
		     			ContactsConstants.ONTACTSAPCUSID = c.getString(3);
		     			ContactsConstants.CONTACTSAPCUSFNAME = c.getString(4);
		     			ContactsConstants.CONTACTSAPCUSLNAME = c.getString(5);
		     			//ContactsConstants.CONTACTFLAGVALUE = c.getString(6);
		     		    sqlitedatabase.close();
		     		 }
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			System.out.println("Error from getting contact details ->"+e.toString());
		}   
    }//fn getContactDetails
    
    public ArrayList getAllContactId(){
    	ArrayList cidArrList = new ArrayList();
    	try {
    		if(cidArrList != null && cidArrList.size() > 0){
    			cidArrList.clear();
    		}
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE, null);
			if(c.getCount() == 0){
				return cidArrList;
			}
			if(c != null){
				if (c.moveToFirst())
				{
				     do {
				    	 int index = c.getColumnIndex(KEY_CONTACTDEVID);
			     		 String dev_no = c.getString(index);
			     		 if(dev_no != null && dev_no.length() > 0){
			     			cidArrList.add(dev_no);
			     		 }
				     } while (c.moveToNext());
				}
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			System.out.println("Error from getting contact details ->"+e.toString());
		}   
    	return cidArrList;
    }//fn getAllContactId
        
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        	db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }
    
    public long insertRow(String cdevid, String csapid, String csapcuid, String csapcufname, 
    		String csapculname)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CONTACTDEVID, cdevid);
        initialValues.put(KEY_CONTACTSAPID, csapid);
        initialValues.put(KEY_CONTACTSAPCUSID, csapcuid);     
        initialValues.put(KEY_CONTACTSAPCUSFNAME, csapcufname); 
        initialValues.put(KEY_CONTACTSAPCUSLNAME, csapculname);  
        //initialValues.put(KEY_CONTACTFLAG, contactflag);
        return sqlitedatabase.insert(DATABASE_TABLE, null, initialValues);
    }

    public Cursor getAllRows()
    {        
        return sqlitedatabase.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_CONTACTDEVID, KEY_CONTACTSAPID, KEY_CONTACTSAPCUSID, KEY_CONTACTSAPCUSFNAME,
                KEY_CONTACTSAPCUSLNAME},
        null, null, null, null, null);
    }//fn getAllRows
    
    public void update_data(String keyid, String csapid, String csapcuid, String csapcufname, 
    		String csapculname){    	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("update "+DATABASE_TABLE+" set "+KEY_CONTACTSAPID+"='"+csapid+"' ,"+KEY_CONTACTSAPCUSID+"='"+csapcuid+"' ,"+KEY_CONTACTSAPCUSFNAME+"='"+csapcufname+"', "+KEY_CONTACTSAPCUSLNAME+"='"+csapculname+"' where "+KEY_CONTACTDEVID+"='"+keyid+"'");
			sqlitedatabase.close();
    	} catch (Exception e) {
			System.out.println("Error update data:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn update_data
    
    public void update_row_data(String keyid, String devid, String csapid, String csapcuid, String csapcufname, String csapculname){    	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("update "+DATABASE_TABLE+" set "+KEY_CONTACTDEVID+"='"+devid+"' ,"+KEY_CONTACTSAPID+"='"+csapid+"' ,"+KEY_CONTACTSAPCUSID+"='"+csapcuid+"' ,"+KEY_CONTACTSAPCUSFNAME+"='"+csapcufname+"', "+KEY_CONTACTSAPCUSLNAME+"='"+csapculname+"' where "+KEY_CONTACTDEVID+"='"+keyid+"'");			
			sqlitedatabase.close();
    	} catch (Exception e) {
			System.out.println("Error update data:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn update_row_data   
    
    public void clearTaskTable()
    {   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+DATABASE_TABLE+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+DATABASE_TABLE);    
			} 			
			//ServiceProConstants.showLog("DELETE FROM "+DATABASE_TABLE+"  "+c.getCount());
			c.close();
			sqlitedatabase.close();
    	} catch (Exception e) {
    		System.out.println("Error in clearTaskTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn clearTaskTable 
    
    /*public String[] getChangesNeedForEdit(){    	
    	String arg[] = null;
    	try {    		
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			Cursor cc = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_CONTACTFLAG+"='true' and " + KEY_CONTACTSAPID+ " != '' and "+ KEY_CONTACTSAPCUSID + "!= ''", null);

			if (cc != null && cc.moveToFirst()) 
			{
				arg = new String[cc.getCount()];
				int i=0;
				do {
			    	 int index = cc.getColumnIndex(KEY_CONTACTDEVID);
		     		 String dev_no = cc.getString(index);
		     		 arg[i] = dev_no;
		     		 i++;
			    } while (cc.moveToNext());
			}
			sqlitedatabase.close();	
			cc.close();
    	} catch (Exception e) {
			System.out.println("Error in getChangesNeed:"+e.toString());
			sqlitedatabase.close();
		} 
    	return arg;
    }//fn getChangesNeedForEdit   
*/    
    /*public String[] getChangesNeedForAdd(){    	
    	String arg[] = null;
    	try {    		
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			Cursor cc = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_CONTACTFLAG+"='true' and " + KEY_CONTACTSAPID+ " = '' and "+ KEY_CONTACTSAPCUSID + "= ''", null);

			if (cc != null && cc.moveToFirst()) 
			{
				arg = new String[cc.getCount()];
				int i=0;
				do {
			    	 int index = cc.getColumnIndex(KEY_CONTACTDEVID);
		     		 String dev_no = cc.getString(index);
		     		 arg[i] = dev_no;
		     		 i++;
			    } while (cc.moveToNext());
			}
			sqlitedatabase.close();	
			cc.close();
    	} catch (Exception e) {
			System.out.println("Error in getChangesNeed:"+e.toString());
			sqlitedatabase.close();
		} 
    	return arg;
    }//fn getChangesNeedForAdd
*/    
    /*public String[] getUpdateCheckForDevToSAP(){    	
    	String arg[] = null;
    	try {    		
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			Cursor cc = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_CONTACTFLAG+"='true' " , null);

			if (cc != null && cc.moveToFirst()) 
			{
				arg = new String[cc.getCount()];
				int i=0;
				do {
			    	 int index = cc.getColumnIndex(KEY_CONTACTDEVID);
		     		 String dev_no = cc.getString(index);
		     		 arg[i] = dev_no;
		     		 i++;
			    } while (cc.moveToNext());
			}
			sqlitedatabase.close();	
			cc.close();
    	} catch (Exception e) {
			System.out.println("Error in getChangesNeed:"+e.toString());
			sqlitedatabase.close();
		} 
    	return arg;
    }//fn getUpdateCheckForDevToSAP   
*/}