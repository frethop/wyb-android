package com.kabestin.android.wristbarcode.googlewallet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.SecurityUtils;

public class GoogleWallet {
	
	public static final HttpTransport httpTransport = new NetHttpTransport();
	public static final JsonFactory jsonFactory = new GsonFactory();

	GoogleCredential credential;
	Context source;
	
	public GoogleWallet(Context source) {
				
		try {
			InputStream p12Stream = source.getAssets().open("wyb.p12");
			RSAPrivateKey serviceAccountPrivateKey = 
					(RSAPrivateKey) SecurityUtils.loadPrivateKeyFromKeyStore(
							SecurityUtils.getPkcs12KeyStore(), p12Stream, "notasecret",
							"privatekey", "notasecret");
//			credential = new GoogleCredential.Builder()
//					  .setTransport(httpTransport)
//					  .setJsonFactory(jsonFactory)
//					  .setServiceAccountId("1034771164092-hc1ffppjbh8icnrvn5b7uqt26k2vrhp5@developer.gserviceaccount.com")
//					  .setServiceAccountScopes("https://www.googleapis.com/auth/wallet_object.issuer")
//					  .setServiceAccountPrivateKey(serviceAccountPrivateKey)
//					  .build();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
