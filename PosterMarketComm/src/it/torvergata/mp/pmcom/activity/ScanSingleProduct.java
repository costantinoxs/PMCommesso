package it.torvergata.mp.pmcom.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.io.IOException;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import it.torvergata.mp.pmcom.Const;
import it.torvergata.mp.pmcom.GenericFunctions;
import it.torvergata.mp.pmcom.R;
import it.torvergata.mp.pmcom.R.id;
import it.torvergata.mp.pmcom.R.layout;



import it.torvergata.mp.pmcom.activity.MainActivity;
import it.torvergata.mp.pmcom.activity.MainActivity.LoadData;
import it.torvergata.mp.pmcom.entity.ListProduct;
import it.torvergata.mp.pmcom.entity.Product;
import it.torvergata.mp.pmcom.helper.CameraPreview;
import it.torvergata.mp.pmcom.helper.Dialogs;
import it.torvergata.mp.pmcom.helper.DrawableManager;
import it.torvergata.mp.pmcom.helper.HttpConnection;
import it.torvergata.mp.pmcom.helper.ProductAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


public class ScanSingleProduct extends Activity {
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private	TextView scanText,tvTitle,tvDescription,tvQuantitative,tvPrice;
	private ImageView iv;
	private Context ctx;
	private String nome;
	private int qnt;
	
	private Dialogs dialogs;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}

	private ListProduct productList;
	private Handler handler;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zbar);
		ctx=this;
		
		productList=new ListProduct();
		Intent intent = getIntent();
		productList  = (ListProduct) intent.getParcelableExtra("PRODUCTLIST");
		
		autoFocusHandler = new Handler();

		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		//productList = new ListProduct();
		
	
		dialogs= new Dialogs();
		
		
		scanText = (TextView) findViewById(R.id.scanText);
		Button ContinueScanButton = (Button) findViewById(R.id.ContinueScanButton);
		
		tvTitle 		= (TextView)findViewById(R.id.title);
		tvDescription 	= (TextView)findViewById(R.id.description);
		iv 				= (ImageView)findViewById(R.id.list_image);
		tvQuantitative 	= (TextView)findViewById(R.id.tvQuantitative);
		tvPrice 		= (TextView)findViewById(R.id.price);
		
		ContinueScanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            		
                    scanText.setText(R.string.tScanning);
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        );
		
		//Handler per il messaggio di risposta del Server, proveniente dal Thread.
		handler = new Handler() {
            @Override
            public void handleMessage(Message mess) {
            	
            	int res = mess.arg1;
               	
            	if(res==Const.KO){
            		//Dialog KO
            		AlertDialog dialogBoxKO = dialogs.GenericErrorDialog(ctx,"Codice Scansionato non trovato");
            		dialogBoxKO.show();
                }
                
            	
                else if(res==Const.TIMEOUT){
                	AlertDialog dialogBox = dialogs.ConnectionTimeout(ctx);
    				dialogBox.show();
                }
                else if(res==Const.KOLIST){
                	//Dialog KOLIST
                	AlertDialog dialogBoxKOLIST = dialogs.GenericErrorDialog(ctx,"L'ordine non contiene il prodotto selezionato");
                	dialogBoxKOLIST.show();
                }
                else if(res==Const.JUSTCHECKED){
                	//Dialog KOLIST
                	scanText.setText("Prodotto scansionato: "+nome);
					
                	AlertDialog alertDialog = new AlertDialog.Builder(ctx)
            		.setTitle(R.string.tWarning)
            		.setMessage("Prodotto già scansionato !")
            		.setIcon(android.R.drawable.ic_dialog_alert)//.setIcon(R.drawable.img_delete)
            		.setPositiveButton(R.string.tOk,
            				new DialogInterface.OnClickListener() {
            					public void onClick(DialogInterface dialog,
            							int whichButton) {
            						dialog.dismiss(); 
            						Intent returnIntent = new Intent();
            	                	returnIntent.putExtra("result", (Parcelable) productList);
            	                	setResult(1,returnIntent);
            	                	finish();
            					}
            				})
            		.create();
            		alertDialog.show();
                }
                else if(res==Const.OKMULTIPLE){
                	scanText.setText("Prodotto scansionato: "+nome);
					
                	//Dialog OKMULTIPLE
                	AlertDialog alertDialog = new AlertDialog.Builder(ctx)
            		.setTitle(R.string.tWarning)
            		.setMessage("Il codice scansionato ha quantità multipla all''interno dell'ordine: "+qnt+" Pz.")
            		.setIcon(android.R.drawable.ic_dialog_alert)//.setIcon(R.drawable.img_delete)
            		.setPositiveButton(R.string.tOk,
            				new DialogInterface.OnClickListener() {
            					public void onClick(DialogInterface dialog,
            							int whichButton) {
            						dialog.dismiss(); 
            						Intent returnIntent = new Intent();
            	                	Log.i("boolean4", ""+productList.get(0).isChecked());
            	                	returnIntent.putExtra("result", (Parcelable) productList);
            	                	setResult(1,returnIntent);
            	                	finish();
            					}
            				})
            		.create();
            		alertDialog.show();
                	
                	
                }
            	
                else {
                	scanText.setText("Prodotto scansionato: "+nome);
					
                	Intent returnIntent = new Intent();
                	returnIntent.putExtra("result", (Parcelable) productList);
                	setResult(1,returnIntent);
                	finish();
                	
                
    		
    			
                	
                	//Si torna all'activity con la lista
                	
                	
                }
                }
                
            
		};
	}


	
	
	public void onResume() {
		super.onResume();

		mCamera = getCameraInstance();

		mPreview = new CameraPreview(ctx, mCamera, previewCb,
				autoFocusCB);
		FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);

		if (preview.getChildCount() > 0) {
			preview.removeAllViews();
		}

		preview.addView(mPreview);

	}
	public void onPause() {
		super.onPause();
		releaseCamera();
	}
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

		
	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
//				previewing = false;
//				mCamera.setPreviewCallback(null);
//				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					String contents = sym.getData();
					String id="";
					if(contents.length()!=Const.ID_PRODUCT_FORMAT){
						
						//do nothing
						//Log.i("ATTENZIONE", "QRCODE SCANSIONATO NON VALIDO");
						
						//AlertDialog dialogBox = NotValidQrCorde();
						//dialogBox.show();
					}
					else{
						playSound(ctx);
						previewing = false;
						mCamera.setPreviewCallback(null);
						mCamera.stopPreview();
						
						
						try{
							id=contents;
						}
						catch(NumberFormatException e){
							Log.d("ERR","Errore");
							
						}
						barcodeScanned = true;
						
							
						//Determiniamo se c'è una connessione ad internet
						boolean isConnected = Const.verifyConnection(ctx);
						if(isConnected){
							//Lancio dell'AsyncTask Thread che effettua il download delle informazioni dal Server
							LoadDataProduct task = new LoadDataProduct();
							task.execute(id);
						}else{
							AlertDialog dialogBox = dialogs.ConnectionNotFound(ctx);
							dialogBox.show();
					
																			
						}
						
					}	
				}
			}

		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	/***
	 * Classe di gestione del Thread che effettua il download dei dati
	 * informativi del prodotto.
	 * 
	 */
	public class LoadDataProduct extends AsyncTask<String, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
		};

		@Override
		protected void onPostExecute(Void result) {
		}

		@Override
		protected Void doInBackground(String... params) {
			String productId = params[0];

			 
			
			try {
				HttpConnection connection = new HttpConnection();
					
				JSONObject json = new JSONObject();
				json.put("id", productId);
							
				JSONObject object = connection.connect("info_download", json, handler,Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);
								
				String result = object.getString("result");
				if (Integer.parseInt(result)==Const.OK){
					// Lettura dell'oggetto Json
					String idProdotto = object.getString("idProdotto");
					
					nome = object.getString("nome");
					double prezzo = object.getDouble("prezzo");
					String scadenza = object.getString("scadenza");
					String disponibilita = object.getString("disponibilita");
					String descrizione = object.getString("descrizione");
					String fileImmagine = object.getString("file_immagine");
					
					
	
					Log.i("idProdotto: ", idProdotto);
					Log.i("nome: ", nome);
					Log.i("prezzo: ", Double.toString(prezzo));
					Log.i("scadenza: ", scadenza);
					Log.i("descrizione: ", descrizione);
					Log.i("disponibilita: ", disponibilita);
					Log.i("file_immagine: ", fileImmagine);
					
					// Creazione del nuovo Prodotto
					Product tempProd= new Product(idProdotto);
					
					tempProd.setId(idProdotto);
					tempProd.setNome(nome);
					tempProd.setPrezzoUnitario(prezzo);
					tempProd.setScadenza(scadenza);
					tempProd.setDescrizione(descrizione);
					tempProd.setDisponibilita(Integer.parseInt(disponibilita));
					tempProd.setFileImmagine(fileImmagine);
	
					
					if(productList.searchById(tempProd.getId())==null){
						//Comunicazione al Thread principale del nome del prodotto
						Message message = handler.obtainMessage(1, Const.KOLIST, 0);
						handler.sendMessage(message);
					
					}else {
						Log.i("boolean1", ""+productList.get(0).isChecked());
		                
						// Si imposta a true la variabile booleana associata al prodotto
						
						int r=productList.searchByIdAndChecked(tempProd.getId());
						if(r==1)
						{
							Message message = handler.obtainMessage(1, Const.JUSTCHECKED, 0);
							handler.sendMessage(message);
						}else{	
							Log.i("boolean2", ""+productList.get(0).isChecked());
			                qnt=productList.searchById(tempProd.getId()).getQuantita();
							if((qnt>1)){	
								Log.i("boolean3", ""+productList.get(0).isChecked());
				                
								Message message = handler.obtainMessage(1, Const.OKMULTIPLE, 0);
								handler.sendMessage(message);
								
							}else{
								Message message = handler.obtainMessage(1, Const.OK, 0);
								handler.sendMessage(message);
							}
						}
					}
				}
				else{
					//Comunicazione al Thread principale del nome del prodotto
					//Message message = handler.obtainMessage(1, Const.KO);
					Message message = handler.obtainMessage(1, Const.KO, 0);
					handler.sendMessage(message);
				}
			}catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
			}

			return null;
		};
	}
	
	
	
	
	
	public void playSound(Context context)  {

		MediaPlayer mPlayer = new MediaPlayer();
	    mPlayer = MediaPlayer.create(context, R.raw.beep);
	    mPlayer.start();

	}
}






