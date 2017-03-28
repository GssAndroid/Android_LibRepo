package com.globalsoft.SalesPro;



import com.globalsoft.SalesPro.Constraints.SalesOrdProPriceConstraints;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

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

public class PriceListDetailViewScreen extends Activity{
	private TextView[] vanStkLblTV;
	private EditText[] vanStkValET;	
	private SalesOrdProPriceConstraints stkCategory = null;	
	private int dispwidth = 300;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {			
        	SalesOrderProConstants.setWindowTitleTheme(this);   
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.pricedetail); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText("Price List Detail ");
			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}			
			stkCategory = (SalesOrdProPriceConstraints) this.getIntent().getSerializableExtra("priceCategoryObj");
			initLayout();
        } catch (Exception de) {
        	SalesOrderProConstants.showErrorLog(de.toString());
        }
    }
	
	private void initLayout(){
		try {
			int cols = 6;
			vanStkLblTV = new TextView[cols];
			vanStkValET = new EditText[cols];			
			try{
				dispwidth = SalesOrderProConstants.getDisplayWidth(this);
				TableLayout tl = (TableLayout)findViewById(R.id.pricedettbllayout1);
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
		            	
					vanStkLblTV[i] = new TextView(this);
					vanStkValET[i] = new EditText(this);
					
					vanStkLblTV[i].setText("");
					vanStkLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
					vanStkLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
					vanStkLblTV[i].setPadding(5,5,5,5); 
					vanStkLblTV[i].setMinWidth(100);
					vanStkLblTV[i].setWidth(labelWidth);
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH)
						vanStkLblTV[i].setTextSize(SalesOrderProConstants.TEXT_SIZE_LABEL);    					  		
					
					vanStkValET[i].setText("");
					vanStkValET[i].setPadding(5,5,5,5);
					//vanStkValET[i].setWidth(160);
					vanStkValET[i].setWidth(editWidth);
					vanStkValET[i].setEnabled(false);
                    vanStkValET[i].setClickable(false);
                    vanStkValET[i].setFocusable(false);
                    
					tr1.addView(vanStkLblTV[i]);
					tr1.addView(vanStkValET[i]);
					tr1.setLayoutParams(linparams);
					
					tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				}
				
				SalesOrderProConstants.showLog("Textview width : "+vanStkLblTV[0].getWidth());
				SalesOrderProConstants.showLog("EditText width : "+vanStkValET[0].getWidth());
				
				vanStkLblTV[0].setText(R.string.SALESORDPRO_PLIST_MATNO);
				vanStkLblTV[1].setText(R.string.SALESORDPRO_PLIST_MATDESC);
				vanStkLblTV[2].setText(R.string.SALESORDPRO_PLIST_RATEAMT);
				vanStkLblTV[3].setText(R.string.SALESORDPRO_PLIST_RATEUNIT);
				//vanStkLblTV[4].setText(R.string.SALESORDPRO_PLIST_CONDPRICEUNIT);
				vanStkLblTV[4].setText(R.string.SALESORDPRO_PLIST_CONDTNUNIT);
				vanStkLblTV[5].setText(R.string.SALESORDPRO_PLIST_KSCHL);
				//vanStkLblTV[6].setText(R.string.SALESORDPRO_PLIST_PLTYP);
				//vanStkLblTV[7].setText(R.string.SALESORDPRO_PLIST_CONDITIONTYPE);
				//vanStkLblTV[7].setText(R.string.SALESORDPRO_PLIST_PRICELISTYPE);
				
				getPriceListDetails();
			}catch(Exception sf){}
		}catch (Exception ssdf) {
			SalesOrderProConstants.showErrorLog("Error in initLayout : "+ssdf.toString());
		}
	}//fn initLayout	
	
	private void getPriceListDetails(){
        try{
            if(stkCategory != null){
            	String data = "";
                for(int k1=0; k1<vanStkValET.length; k1++){
                    if(vanStkValET[k1] != null){
                    	data = "";
                        switch(k1){
                            case 0:
                            	data = stkCategory.getMaterialNo();
                            	vanStkValET[k1].setText(data);
                                break;
                            case 1:
                            	data = stkCategory.getMattDesc();
                            	vanStkValET[k1].setText(data);
                                break;
                            case 2:
                            	String data5 = stkCategory.getRateunit();
                            	data = stkCategory.getRateAmount()+""+data5+"";
                            	vanStkValET[k1].setText(data);
                                break;
                           /* case 3:
                            	data = stkCategory.getRateunit();
                            	vanStkValET[k1].setText(data);
                                break;*/
                            /*case 3:
                            	data = stkCategory.getCondPricingUnit();
                            	vanStkValET[k1].setText(data);
                                break;*/
                            case 3:
                            	//String data6 = stkCategory.getPriceListType();
                            	data = stkCategory.getKSCHLText();
                            	vanStkValET[k1].setText(data);
                                break;
                            case 4:
                            	 String datan = stkCategory.getCondPricingUnit();
                             	 data = stkCategory.getConditionUnit()+" "+"("+datan+")";
                             	 vanStkValET[k1].setText(data);
                                 break;
                            case 5:
                            	String data4=stkCategory.getConditionType();
                            	data = stkCategory.getPLTYPText()+" "+"("+data4+")";
                            	vanStkValET[k1].setText(data);
                                break;
                            /*case 7:
                            	data = stkCategory.getConditionType();
                            	vanStkValET[k1].setText(data);
                                break;*/
                            /*case 8:
                            	data = stkCategory.getPriceListType();
                            	vanStkValET[k1].setText(data);
                                break;*/
                        }

                    	/*if((data == null) || (data.equalsIgnoreCase(""))){
                    		if(vanStkLblTV[k1] != null){
                    			vanStkLblTV[k1].setVisibility(View.GONE);
                        		vanStkValET[k1].setVisibility(View.GONE);
                    		}
                    	}*/
                        vanStkValET[k1].setEnabled(false);
                        //vanStkValET[k1].setClickable(false);
                        vanStkValET[k1].setTextColor(getResources().getColor(R.color.bluelabel));
                    }
                }
            }
        }
        
       
        catch(Exception adf){
        	SalesOrderProConstants.showErrorLog("Error in getVanStockDetails : "+adf.toString());
        }
    }//fn getPriceListDetails	
}