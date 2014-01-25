package it.torvergata.mp.pmcom.activity;

import it.torvergata.mp.pmcom.GenericFunctions;
import it.torvergata.mp.pmcom.R;
import it.torvergata.mp.pmcom.R.layout;
import it.torvergata.mp.pmcom.R.menu;





import it.torvergata.mp.pmcom.entity.ListProduct;
import it.torvergata.mp.pmcom.entity.Product;
import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
 

public class ProductDetailActivity extends Activity {
	   
	private Product prod;
	private TextView tvTitle,tvDescription,tvPrice,tvQuantitative,tvSimplePrice;
	private Button btnListProduct,btnChangeQuantitativeP,btnChangeQuantitativeM;
	private EditText etQuantitative;
	private ImageView ivImage;
	private ListProduct productlist;
	private int position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		
		Intent intent = getIntent();
		prod  = (Product) intent.getParcelableExtra("PRODUCT");
		   
		 
		
	    tvTitle 			= (TextView)  findViewById(R.id.tvTitleDetail);
    
	    //Inizializzazione
  		ivImage = (ImageView)   findViewById(R.id.ivDetailImage);
  		tvTitle = (TextView)   findViewById(R.id.tvTitleDetail);
  		tvDescription = (TextView)   findViewById(R.id.tvDescriptionDetail);
  		tvPrice = (TextView)   findViewById(R.id.tvTotalPrice);
  		tvQuantitative = (TextView)   findViewById(R.id.tvQuantitativeDetail);
  		tvSimplePrice = (TextView)   findViewById(R.id.tvSimplePrice);
  		btnListProduct = (Button)   findViewById(R.id.btnListProduct);
  		
        
		ivImage.setImageDrawable(prod.getImmagine());
		tvTitle.setText(prod.getNome());
		tvDescription.setText(prod.getDescrizione());
		tvPrice.setText(getString(R.string.tvTotal)+" "+GenericFunctions.currencyStamp(prod.getPrezzoTotale())+"  "+getString(R.string.Euro));
		tvQuantitative.setText(getString(R.string.tQuantitative)+" "+prod.getQuantita());
		tvSimplePrice.setText(getString(R.string.tPrice)+" "+GenericFunctions.currencyStamp(prod.getPrezzoUnitario())+" "+getString(R.string.Euro));
			
		btnListProduct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
			
	  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_detail, menu);
		return true;
	}

}


