package it.torvergata.mp.pmcom.entity;


import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.pmcom.R;
import it.torvergata.mp.pmcom.crypto.CryptoSha256;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class ListProduct extends ArrayList<Product> implements Parcelable{
	private CryptoSha256 crypto;
	double totalPrice=0.00;
	int count=0;
	int associateOrderId=0;
	String associateOrderDate="";
	int associateOrderState=-1;
	
	public void setAssociateOrderState(int associateOrderState) {
		this.associateOrderState = associateOrderState;
	}

	public String getAssociateOrderDate() {
		return associateOrderDate;
	}

	public void setAssociateOrderDate(String associateOrderDate) {
		this.associateOrderDate = associateOrderDate;
	}

	public String getAssociateOrderTime() {
		return associateOrderTime;
	}

	public void setAssociateOrderTime(String associateOrderTime) {
		this.associateOrderTime = associateOrderTime;
	}

	String associateOrderTime="";
	
	public int getAssociateOrderId() {
		return associateOrderId;
	}

	public void setAssociateOrderId(int associateOrderId) {
		this.associateOrderId = associateOrderId;
	}

	public ListProduct(){
		super();
		crypto=new CryptoSha256();
		
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}

	public int getCount() {
		return  this.count;
	}
	public int getProductsCount() {
	
		int result=0;
		for(int i=0;i<this.size();i++)
		{
			result+=this.get(i).getQuantita();
		}
		return  result;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean add(Product prod){
		super.add(prod);
		
		totalPrice+=prod.getPrezzoTotale();
		totalPrice=round(totalPrice,2);
		for (int i=0;i<prod.getQuantita();i++){
			count++;
		}
		return true;
		
	}
	
	public Product searchByIdAndUpdateLast(String id){
		
		for(int i=0;i<super.size();i++){
			if((super.get(i).getId().equals(id))){
				Product t=super.get(i);
				updateLast(i);
				return t;	
			}
		}
		
		return null;
		
	}
	
	public Product remove(int position){
		int q=super.get(position).getQuantita();
		totalPrice-=super.get(position).getPrezzoTotale();
		totalPrice=round(totalPrice,2);
		for (int i=0;i<q;i++){
			count--;
		}
		super.remove(position);
		return null;
	}
	private double round(double d, int numbersAfterDecimalPoint) {
	    double n = Math.pow(10, numbersAfterDecimalPoint);
	    double d2 = d * n;
	    long l = (long) d2;
	    return ((double) l) / n;
	}

	public void setIncrementTotalPrice(double prezzoUnitarioProdotto) {
		// TODO Auto-generated method stub
		count++;
		totalPrice+=prezzoUnitarioProdotto;
	}

	public void updateLast(int pos) {
		Product t=this.get(pos);
		this.remove(pos);
		this.add(t);
		
	}

	public JSONObject getListIdForOrder(String user) {
		JSONArray jsonArray= new JSONArray();
		JSONObject jsonObjH = new JSONObject();
		String toHash="";
		JSONObject jsonObjU = new JSONObject();
		JSONObject json = new JSONObject();
		
		long msTime = System.currentTimeMillis();  

		Date anotherCurDate = new Date(msTime);  
		SimpleDateFormat formatter = new SimpleDateFormat("dMMMMyyyHHmm");  
		String formattedDateString = formatter.format(anotherCurDate);
		Log.i("Data nell'hash:",formattedDateString);
		
		
		/*
		 * Si Crea L'hash dalla stringa contenente :
		 * user
		 * Date
		 * Time
		 * idProduct#1
		 * QuantitativeProduct#1
		 * idProduct#2
		 * QuantitativeProduct#2
		 * .
		 * .
		 * .
		 * idProduct#n
		 * QuantitativeProduct#n
		 * 
		*/
		toHash+=user;
		toHash+=formattedDateString;
		
		try {
			
			jsonObjU.put("user", user);
		
		
		for(int i=0;i<this.size();i++){
			JSONObject temp = new JSONObject();
			int qnt=this.get(i).getQuantita();
			String id=this.get(i).getId();
			toHash+=id;
			toHash+=qnt;
			temp.put("Q",qnt );
			temp.put("id",id );
			jsonArray.put(temp);
		}
		Log.i("HashToSend",toHash);
		String hashToSendCrypto=crypto.encrypt(toHash);
		Log.i("HashToSend Crittogr",crypto.encrypt(toHash));
		
		jsonObjH.put("hashOrder", hashToSendCrypto);
		json.put("HashID"	, jsonObjH);
		json.put("User"		, jsonObjU);
		json.put("Products"	, jsonArray);
		
		
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
		// TODO Auto-generated method stub
		
	}

	public void setDecrementTotalPrice(double prezzoUnitarioProdotto) {
		// TODO Auto-generated method stub
		count--;
		totalPrice-=prezzoUnitarioProdotto;
	}

	public String getAssociateOrderState() {
		String result;
		result= GenericFunctions.convertOrderState(associateOrderState);
		return result;
	}

	public void removeById(String id) {
		// TODO Auto-generated method stub
		for(int i=0;i<this.size();i++){
			if(this.get(i).getId().compareTo(id)==0){
				this.remove(i);
				break;
			}
		}
	}

	public void print(String g) {
		// TODO Auto-generated method stub
		Log.i(g, " ");
		for(int i=0;i<this.size();i++){
			Log.i(g, "PRODOTTO NUMERO "+i+" : "+this.get(i).getNome()+" Quantità: "+this.get(i).getQuantita());
		}
		
	}
	public void updateChecked(ListProduct localProductList) {
		for(int i=0;i<this.size();i++){
			for(int j=0;j<localProductList.size();j++){
				if(this.get(i).getId().compareTo(localProductList.get(j).getId())==0){
					localProductList.get(j).setChecked(true);
					localProductList.get(j).setQuantita(this.get(i).getQuantita());
					break;
				}
			}
		}
	}

	public Product getById(String id) {
		Product p = null;
		for(int i = 0; i<this.size();i++){
			if(this.get(i).getId().compareTo(id)== 0){
				p = this.get(i);
				break;
			}
		}
		return p;
	}

	@Override
	public int describeContents() {
		return 0;
	}



	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		Log.i("WRITEEEEEEEEEEEEE","");
		dest.writeInt(this.size());
		for(int i=0;i<this.size();i++){
			Log.i("Prodotto i:",""+i);
			dest.writeString(this.get(i).getId());
			dest.writeString(this.get(i).getNome());
			dest.writeDouble(this.get(i).getPrezzoUnitario());
			dest.writeInt(this.get(i).getQuantita());
			dest.writeString(this.get(i).getScadenza());
			dest.writeInt(this.get(i).getDisponibilita());
			dest.writeString(this.get(i).getDescrizione());
			dest.writeString(this.get(i).getFileImmagine());
			dest.writeByte((byte) (this.get(i).isChecked() ? 1 : 0));
			Log.i("ID",""+this.get(i).getId());
			Log.i("Nome",""+this.get(i).getNome());
			Log.i("Prezzo",""+this.get(i).getPrezzoUnitario());
			Log.i("Quantita",""+this.get(i).getQuantita());
			Log.i("Scadenza",""+this.get(i).getScadenza());
			Log.i("Disponibilità",""+this.get(i).getDisponibilita());
			Log.i("Descrizione",""+this.get(i).getDescrizione());
			Log.i("fileImmagina",""+this.get(i).getFileImmagine());
			
//			Bitmap bitmap = (Bitmap)((BitmapDrawable) this.immagine).getBitmap();
//			dest.writeParcelable(bitmap, flags);
		}
		
	}
	 private ListProduct(Parcel in) {
		 super();
		 Log.i("READDDDDDDDDDDDDDDDD","");
		 int totale=in.readInt();
		 Log.i("TOTALE",""+totale);
		 for(int i=0;i<totale;i++){
			Log.i("Prodotto i:",""+i);
			 String id= in.readString();
			 Log.i("ID",id);
			 Product temp = new Product(id);
			 
			 
			 String nome=in.readString();
			 temp.setNome(nome);
			 Log.i("nome",nome);
			 
			 double Prezzo=in.readDouble();
			 temp.setPrezzoUnitario(Prezzo);
			 Log.i("Prezzo",""+Prezzo);
			 
		     int Quantita=in.readInt();
		     temp.setQuantita(Quantita);
		     Log.i("Quantita",""+Quantita);
		     
		     String Scadenza=in.readString();
		     temp.setScadenza(Scadenza);
		     Log.i("Scadenza",Scadenza);
		     
		     int Disponibilita=in.readInt();
		     temp.setDisponibilita(Disponibilita);
		     Log.i("Disponibilita",""+Disponibilita);
		     
		     String Descrizione=in.readString();
		     temp.setDescrizione(Descrizione);
		     Log.i("Descrizione",Descrizione);
		     
		     String file=in.readString();
		     temp.setFileImmagine(file);
		     Log.i("file",file);
		     
		     boolean check = in.readByte() != 0;
		     temp.setChecked(check);
		     Log.i("check",""+check);
		     
		     
		     //Bitmap bitmap = (Bitmap)in.readParcelable(getClass().getClassLoader());
//		     immagine = new BitmapDrawable(bitmap);
		     this.add(temp);
		     this.print("Lista");
		 }
		 
		}
	 public static final Parcelable.Creator<ListProduct> CREATOR = new Parcelable.Creator<ListProduct>() {
	     public ListProduct createFromParcel(Parcel in) {
	         return new ListProduct(in);
	     }

	     public ListProduct[] newArray(int size) {
	         return new ListProduct[size];
	     }
	 };

	public Product searchById(String id) {
		// TODO Auto-generated method stub
		for(int i=0;i<this.size();i++){
			if((this.get(i).getId().equals(id))){
				return this.get(i);	
			}
		}
		return null;
	}

	public int searchByIdAndChecked(String id) {
		// TODO Auto-generated method stub
		int r=0;
		for(int i=0;i<this.size();i++){
			if((this.get(i).getId().equals(id))){
				Log.i("Prod checkid", ""+this.get(i).getId());
				Log.i("Prod check1", ""+this.get(i).isChecked());
				if(this.get(i).isChecked())r=1;
				this.get(i).setChecked(true);
				Log.i("Prod check2", ""+this.get(i).isChecked());
			}
		}
		return r;
	}

	
}
	

