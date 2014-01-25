package it.torvergata.mp.pmcom.activity;

import it.torvergata.mp.pmcom.Const;
import it.torvergata.mp.pmcom.GenericFunctions;
import it.torvergata.mp.pmcom.R;
import it.torvergata.mp.pmcom.R.layout;
import it.torvergata.mp.pmcom.R.menu;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.app.Activity;
import android.view.Menu;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import it.torvergata.mp.pmcom.activity.ScanOrderActivity.LoadDataProduct;
import it.torvergata.mp.pmcom.entity.Customer;
import it.torvergata.mp.pmcom.entity.ListProduct;
import it.torvergata.mp.pmcom.entity.Product;
import it.torvergata.mp.pmcom.helper.Dialogs;
import it.torvergata.mp.pmcom.helper.HttpConnection;
import it.torvergata.mp.pmcom.helper.ProductAdapter;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

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
 


public class ListProductForDeliveryActivity extends Activity {
	
	
	private ListProduct productList;
	private TextView totalPrice;
	private Button btnAdd,BtnContinue;
	private LinearLayout mLinearLayout;
	private ProductAdapter adapter;
	private Dialogs dialogs;
	private Context ctx;
	private ListView list;
	private Handler handler;
	private int orderID;
	private Customer customer;
	@Override
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx=this;
		setContentView(R.layout.activity_list_product_for_delivery);
		  
		   productList=new ListProduct();
		   Intent intent = getIntent();
		   orderID=intent.getIntExtra("orderID", 1);
		   customer  = (Customer) intent.getParcelableExtra("CUSTOMER");
		   
		   productList  = (ListProduct) intent.getParcelableExtra("PRODUCTLIST");
		   
		   dialogs=new Dialogs();
	         
	    	totalPrice 			= (TextView)  findViewById(R.id.tvTotalPrice);
			
	    	Button btnCompleteOrder 	= (Button)  findViewById(R.id.btnComplete);
	    	TextView tvName 	= (TextView)  findViewById(R.id.tvNameC);
	    	TextView tvSurName 	= (TextView)  findViewById(R.id.tvSurnameC);
	    	TextView tvUserName 	= (TextView)  findViewById(R.id.tvUserC);
	    	TextView tvEmail 	= (TextView)  findViewById(R.id.tvEmailC);
			
			list = (ListView)  findViewById(id.list);
			list.setCacheColorHint(00000000);
			
			adapter =new ProductAdapter(ctx,
					R.layout.new_list_item, productList);
			list.setAdapter(adapter);
			
			setTotalPrice();
			
			tvName.setText(customer.getName());
			tvSurName.setText(customer.getSurname());
			tvUserName.setText(customer.getUser());
			tvEmail.setText(customer.getEmail());
			
			
			
			
			//Handler per il messaggio di risposta del Server, proveniente dal Thread.
			handler = new Handler() {
	            @Override
	            public void handleMessage(Message mess) {
	            	
	            	int res = mess.arg1;
	               	
	            	if(res==Const.KO){
	            		
	                }
	            	
	                else if(res==Const.TIMEOUT){
	                	AlertDialog dialogBox = dialogs.ConnectionTimeout(ctx);
	    				dialogBox.show();
	                }
	                else {
	                	//Dialog ORDERCOMPLETE
	                	AlertDialog alertDialog = new AlertDialog.Builder(ctx)
	            		.setTitle(R.string.app_name)
	            		.setMessage("Ordine Ritirato")
	            		.setIcon(android.R.drawable.ic_dialog_info)//.setIcon(R.drawable.img_delete)
	            		.setPositiveButton(R.string.tOk,
	            				new DialogInterface.OnClickListener() {
	            					public void onClick(DialogInterface dialog,
	            							int whichButton) {
	            						dialog.dismiss(); 
	            						Intent returntoStartIntent = new Intent(ctx,PreScanningActivity.class);
	            						startActivity(returntoStartIntent);
	            						finish();
	            					}
	            				})
	            		.create();
	            		alertDialog.show();
						
	                }
	                }
	                
	            
			};
			
			
			
			btnCompleteOrder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
							//Lancio dell'AsyncTask Thread che effettua l'invio della notifica
							sendNotification task = new sendNotification();
							task.execute(""+orderID);
						
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
	public class sendNotification extends AsyncTask<String, Void, Void> {
		
		@Override
		protected void onPreExecute() {
		};

		@Override
		protected void onPostExecute(Void result) {
		}

		@Override
		protected Void doInBackground(String... params) {
			String orderId = params[0];
			
				try {
				HttpConnection connection = new HttpConnection();
			
				//Invio notifica ordine: stato Preso in carico (2)
				JSONObject jsonNot = new JSONObject();
				jsonNot.put("idOrder", ""+orderId);
				jsonNot.put("stato", "4");
				JSONObject arrayNotif = connection.connect("inviaNotifiche", jsonNot, handler, Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);
								
				//Comunicazione al Thread principale del nome del prodotto
				Message message = handler.obtainMessage(1, Integer.parseInt(orderId), 0);
				
				handler.sendMessage(message);
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
			}

			return null;
		};
	}
	
}

