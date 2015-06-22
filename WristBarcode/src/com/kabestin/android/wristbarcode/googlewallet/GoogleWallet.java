package com.kabestin.android.wristbarcode.googlewallet;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.walletobjects.Walletobjects;
import com.google.api.services.walletobjects.model.LoyaltyClass;
import com.google.api.services.walletobjects.model.LoyaltyClassListResponse;
import com.google.api.services.walletobjects.model.LoyaltyObject;
import com.google.api.services.walletobjects.model.LoyaltyObjectListResponse;
import com.google.gson.Gson;
import com.google.wallet.objects.webservice.WebserviceResponse;

public class GoogleWallet {
	
	public static final HttpTransport httpTransport = new NetHttpTransport();
	public static final JsonFactory jsonFactory = new GsonFactory();
	
	public static final List<String> scopes = Collections.unmodifiableList(Arrays.asList(
		      "https://www.googleapis.com/auth/wallet_object.issuer",
		      "https://www.googleapis.com/auth/wallet_object_sandbox.issuer"));

	GoogleCredential credential;
	Walletobjects client;
	Context source;
	
	private List<GenericJson> loyaltyClasses = new ArrayList<GenericJson>();
	private List<GenericJson> offerClasses = new ArrayList<GenericJson>();
	private List<GenericJson> giftCardClasses = new ArrayList<GenericJson>();
	private List<GenericJson> genericClasses = new ArrayList<GenericJson>();
	private List<GenericJson> boardingPassClasses = new ArrayList<GenericJson>();

	private List<GenericJson> loyaltyObjects = new ArrayList<GenericJson>();
	private List<GenericJson> offerObjects = new ArrayList<GenericJson>();
	private List<GenericJson> giftCardObjects = new ArrayList<GenericJson>();
	private List<GenericJson> genericObjects = new ArrayList<GenericJson>();
	private List<GenericJson> boardingPassObjects = new ArrayList<GenericJson>();

	private WebserviceResponse webserviceResponse;

	private transient Gson gson = new Gson();
	
	public GoogleWallet(Context source) {
				
		try {
			InputStream p12Stream = source.getAssets().open("wyb.p12");
			RSAPrivateKey serviceAccountPrivateKey = 
					(RSAPrivateKey) SecurityUtils.loadPrivateKeyFromKeyStore(
							SecurityUtils.getPkcs12KeyStore(), p12Stream, "notasecret",
							"privatekey", "notasecret");
			credential = new GoogleCredential.Builder()
					  .setTransport(httpTransport)
					  .setJsonFactory(jsonFactory)
					  .setServiceAccountId("1034771164092-hc1ffppjbh8icnrvn5b7uqt26k2vrhp5@developer.gserviceaccount.com")
					  .setServiceAccountScopes(scopes)
					  .setServiceAccountPrivateKey(serviceAccountPrivateKey)
					  .build();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    String key = credential.toString();
        client =
	          new Walletobjects.Builder(httpTransport, jsonFactory, credential)
	              //.setRootUrl("https://www-googleapis-staging.sandbox.google.com")
	              .setApplicationName("wyb").build();
		
	}
	
	public ArrayList<String> getLoyaltyClassList() {
		try {
			LoyaltyClassListResponse classes = client.loyaltyclass()
					  .list(1234567L)
					  .setMaxResults(25)
					  .execute();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> getLoyaltyObjectList() {
		try {
			LoyaltyObjectListResponse objs = client.loyaltyobject()
					  .list("1234567.ExampleClass")
					  .setMaxResults(25)
					  .execute();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public LoyaltyClass getLoyaltyClass(String issuerID) {
		try {
			LoyaltyClass cls = client.loyaltyclass().get("2945482443380251551.ExampleClass1").execute();
			return cls;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public LoyaltyObject getLoyaltyObject() {
		try {
			LoyaltyObject obj = client.loyaltyobject().get("2945482443380251551.ExampleObject1").execute();
			return obj;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	

}
