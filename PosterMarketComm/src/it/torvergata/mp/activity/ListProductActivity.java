package it.torvergata.mp.activity;

import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.view.Menu;

import java.text.DecimalFormat;

import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.ProductAdapter;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
 


public class ListProductActivity extends Activity {
	
	
	private ListProduct productList;
	private TextView totalPrice;
	private Button btnAdd,BtnContinue;
	private LinearLayout mLinearLayout;
	private ProductAdapter adapter;
	private Dialogs dialogs;
	private Context ctx;
	private ListView list;
	@Override
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (requestCode == 1) {
				if (resultCode == 1) {
					productList=data.getParcelableExtra("result");
					Log.i("Boolean5",""+productList.get(0).isChecked());
					adapter =new ProductAdapter(ctx,
							R.layout.new_list_item, productList);
					list.setAdapter(adapter);
					
				}
			}
		}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx=this;
		setContentView(R.layout.tab_frag_scan_mode_list_layout);
		  
		   productList=new ListProduct();
		   Intent intent = getIntent();
		   productList  = (ListProduct) intent.getParcelableExtra("PRODUCTLIST");
		   
		   dialogs=new Dialogs();
	        
	        
	    	totalPrice 			= (TextView)  findViewById(R.id.tvTotalPrice);
			
	    	Button btnScanCode 		= (Button)  findViewById(R.id.btnScan);
			Button btnCompleteOrder 	= (Button)  findViewById(R.id.btnComplete);
			
			list = (ListView)  findViewById(id.list);
			list.setCacheColorHint(00000000);
			
			adapter =new ProductAdapter(ctx,
					R.layout.new_list_item, productList);
			list.setAdapter(adapter);
			
			setTotalPrice();
			
			list.setOnItemLongClickListener(new OnItemLongClickListener() {


				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int arg2, long arg3) {
					
					final AlertDialog dialogBox = dialogs.DeleteDialog(arg2,productList,ctx);
					dialogBox.show();
					Button deleteButton = dialogBox
							.getButton(DialogInterface.BUTTON_POSITIVE);
					deleteButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try{
							
							productList.remove(arg2);
							adapter.notifyDataSetChanged();
							setTotalPrice();
							}catch (IndexOutOfBoundsException e){
								adapter.notifyDataSetChanged();
							}
							dialogBox.dismiss();	
						}
					});
					
					return false;
				}
			});
			
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
				}
			});
			
			btnScanCode.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ctx, ScanSingleProduct.class);
            		intent.putExtra("PRODUCTLIST",(Parcelable) productList);
            		startActivityForResult(intent, 1);
    				
				}
			});
		
			
			btnCompleteOrder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
	        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_product, menu);
		return true;
	}
	
	public void setTotalPrice(){
		String price = GenericFunctions.currencyStamp(productList.getTotalPrice());
		totalPrice.setText(getString(R.string.tvTotal)+" "+price+" "+getString(R.string.Euro));
		
	}
}

