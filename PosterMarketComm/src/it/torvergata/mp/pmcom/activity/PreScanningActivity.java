package it.torvergata.mp.pmcom.activity;

import it.torvergata.mp.pmcom.R;
import it.torvergata.mp.pmcom.R.layout;
import it.torvergata.mp.pmcom.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PreScanningActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Associazione del layout
				setContentView(R.layout.tab_frag1_layout);

				Button btnOrder = (Button) findViewById(R.id.btnPreparation);
				Button btnDelivery = (Button) findViewById(R.id.btnDelivery);
	
				btnOrder.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//Si lancia la transizione verso il tab di acquisizione
						Intent intent = new Intent(getBaseContext(), ScanOrderActivity.class);
						intent.putExtra("choice", 1);
						startActivity(intent);
						finish();

					}
				});
				btnDelivery.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//Si lancia la transizione verso il tab di acquisizione
						Intent intent = new Intent(getBaseContext(), ScanOrderActivity.class);
						intent.putExtra("choice", 2);
						startActivity(intent);
						finish();

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pre_scanning, menu);
		return true;
	}

}
