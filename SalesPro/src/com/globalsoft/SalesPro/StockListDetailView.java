package com.globalsoft.SalesPro;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;


import com.globalsoft.SalesPro.Constraints.SalesOrdProStockConstraints;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;


public class StockListDetailView extends Activity{
	private TextView[] inStkLblTV;
	private EditText[] inStkValET;
	private SalesOrdProStockConstraints stkCategory=null;
	private int dispwidth = 300;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	
        	SalesOrderProConstants.setWindowTitleTheme(this);   
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.stockdetail); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText("Inventory Detail Screen");

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			
			stkCategory = (SalesOrdProStockConstraints) this.getIntent().getSerializableExtra("stkCategory");
			initLayout();
        } catch (Exception de) {
        	SalesOrderProConstants.showErrorLog(de.toString());
        }
    }
	
	private void initLayout(){
		try {
			int cols = 8;
			inStkLblTV = new TextView[cols];
			inStkValET = new EditText[cols];			
			try{
				dispwidth = SalesOrderProConstants.getDisplayWidth(this);
				TableLayout tl = (TableLayout)findViewById(R.id.stckdettbllayout1);
				if(dispwidth < SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH)
					tl.setColumnStretchable(1, true);
				
				TableRow tr1 = new TableRow(this);
				tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				int labelWidth = 100, editWidth = 200;
				SalesOrderProConstants.showLog("dispwidth : "+dispwidth);
				if(dispwidth > 0){
					labelWidth = dispwidth-editWidth;
					if(labelWidth < 100)
						labelWidth = 100;
					else if(labelWidth > 160)
						labelWidth = 160;
					
					editWidth = dispwidth-labelWidth;
					if(editWidth < 160)
						editWidth = 160;
					else if(editWidth > 250)
						editWidth = 250;
					
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
						labelWidth = 220;
						editWidth = 300;
					}
				}
				SalesOrderProConstants.showLog("labelWidth : "+labelWidth+" : editWidth : "+editWidth);
				
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5; 
				
				for(int i=0; i<cols; i++){
					tr1 = new TableRow(this);
		            	
					inStkLblTV[i] = new TextView(this);
					inStkValET[i] = new EditText(this);
					
					inStkLblTV[i].setText("");
					inStkLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
					inStkLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
					inStkLblTV[i].setPadding(5,5,5,5); 
					inStkLblTV[i].setMinWidth(100);
					inStkLblTV[i].setWidth(labelWidth);
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH)
						inStkLblTV[i].setTextSize(SalesOrderProConstants.TEXT_SIZE_LABEL);    					  		
					
					inStkValET[i].setText("");
					inStkValET[i].setPadding(5,5,5,5);
					//inStkValET[i].setWidth(160);
					inStkValET[i].setWidth(editWidth);
					inStkValET[i].setEnabled(false);
                    inStkValET[i].setClickable(false);
                    inStkValET[i].setFocusable(false);
                    
					tr1.addView(inStkLblTV[i]);
					tr1.addView(inStkValET[i]);
					tr1.setLayoutParams(linparams);
					
					tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				}
				
				SalesOrderProConstants.showLog("Textview width : "+inStkLblTV[0].getWidth());
				SalesOrderProConstants.showLog("EditText width : "+inStkValET[0].getWidth());
				
				inStkLblTV[0].setText(R.string.SALESORDPRO_STKLIST_MATNO);
				inStkLblTV[1].setText(R.string.SALESORDPRO_STKLIST_MATDESC);
				inStkLblTV[2].setText(R.string.SALESORDPRO_STKLIST_STOCK);
				inStkLblTV[3].setText(R.string.SALESORDPRO_STKLIST_UNIT);
				inStkLblTV[4].setText(R.string.SALESORDPRO_STKLIST_INTRANSIT);
				//inStkLblTV[4].setText(R.string.SALESORDPRO_STKLIST_MATDESC);
				inStkLblTV[5].setText(R.string.SALESORDPRO_STKLIST_IN_INSP);
				/*inStkLblTV[6].setText(R.string.SALESORDPRO_STKLIST_SLOC);
				inStkLblTV[7].setText(R.string.SALESORDPRO_STKLIST_PLANT);*/
				inStkLblTV[6].setText(R.string.SALESORDPRO_STKLIST_PLANT_DESC);
				inStkLblTV[7].setText(R.string.SALESORDPRO_STKLIST_SLOCDESC);
				getStockDetails();
			}catch(Exception sf){}
		}catch (Exception ssdf) {
			SalesOrderProConstants.showErrorLog("Error in initLayout : "+ssdf.toString());
		}
	}//fn initLayout	
	
	private void getStockDetails(){
        try{
            if(stkCategory != null){            	
                for(int k1=0; k1<inStkValET.length; k1++){
                    if(inStkValET[k1] != null){
                    	String data = "";
                        switch(k1){
                            case 0:
                            	data = stkCategory.getMaterialNo();
                            	inStkValET[k1].setText(data);
                                break;
                            case 1:
                            	data = stkCategory.getMattDesc();
                            	inStkValET[k1].setText(data);
                                break;
                            	
                            case 2:
                            	data = stkCategory.getStock();
                            	inStkValET[k1].setText(data);
                                break;
                            	
                            case 3:
                            	data = stkCategory.getMeasureUnit();
                            	inStkValET[k1].setText(data);
                                break;
                            	
                            case 4:
                            	data = stkCategory.getStockInTransfer();
                            	inStkValET[k1].setText(data);
                                break;
                            	/*data = stkCategory.getMattDesc();
                            	inStkValET[k1].setText(data);
                                break;*/
                            case 5:
                            	data = stkCategory.getStockInQualityInsp();
                            	inStkValET[k1].setText(data);
                                break;
                            case 6:
                            	String data3=stkCategory.getStorageLocation();
                            	data = stkCategory.getStorageLocationDesc()+"("+data3+")";
                            	inStkValET[k1].setText(data);
                                break;
                            case 7:
                            	String data4=stkCategory.getPlant();
                            	data = stkCategory.getPlantDesc()+"("+data4+")";
                            	inStkValET[k1].setText(data);
                                break;
                        }  
                           /* case 8:
                            	data = stkCategory.getPlantDesc();
                            	inStkValET[k1].setText(data);
                                break;*/
                                
                           /* case 9:
                            	data = stkCategory.getStorageLocationDesc();
                            	inStkValET[k1].setText(data);
                                break;*/
                        

                    	/*if((data == null) || (data.equalsIgnoreCase(""))){
                    		if(inStkLblTV[k1] != null){
                    			inStkLblTV[k1].setVisibility(View.GONE);
                        		inStkValET[k1].setVisibility(View.GONE);
                    		}
                    	}*/
                        inStkValET[k1].setEnabled(false);
                        //inStkValET[k1].setClickable(false);
                        inStkValET[k1].setTextColor(getResources().getColor(R.color.bluelabel));
                    }
                }
            }
        }
        catch(Exception adf){
        	SalesOrderProConstants.showErrorLog("Error in getVanStockDetails : "+adf.toString());
        }
    }//fn getVanStockDetails
}