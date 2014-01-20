package it.torvergata.mp.activity;

import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;
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

				Button mButton = (Button) findViewById(R.id.btnQrCode);
				mButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//Si lancia la transizione verso il tab di acquisizione
						Intent intent = new Intent(getBaseContext(), ScanningActivity.class);
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
