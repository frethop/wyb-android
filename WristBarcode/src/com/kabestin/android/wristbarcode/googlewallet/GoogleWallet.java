package com.kabestin.android.wristbarcode.googlewallet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;

public class GoogleWallet {

	GoogleCredential credential;
	
	public GoogleWallet() {
		
//		HttpTransport httpTransport = new HttpTransport() {
//			
//			@Override
//			protected LowLevelHttpRequest buildRequest(String arg0, String arg1)
//					throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
//		JsonFactory jsonFactory = new JsonFactory() {
//			
//			@Override
//			public JsonParser createJsonParser(InputStream arg0, Charset arg1) 
//					throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public JsonParser createJsonParser(Reader arg0) throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public JsonParser createJsonParser(String arg0) throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public JsonParser createJsonParser(InputStream arg0) throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public JsonGenerator createJsonGenerator(OutputStream arg0, Charset arg1)
//					throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public JsonGenerator createJsonGenerator(Writer arg0) throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
//		
//		try {
//			credential = new GoogleCredential.Builder().setTransport(httpTransport)
//					  .setJsonFactory(jsonFactory)
//					  .setServiceAccountId("ServiceAccountEmail@developer.gserviceaccount.com")
//					  .setServiceAccountScopes("https://www.googleapis.com/auth/wallet_object.issuer")
//					  .setServiceAccountPrivateKeyFromP12File(new File("/example/path/to/yourp12file.p12"))
//					  .build();
//		} catch (GeneralSecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}
