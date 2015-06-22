package com.kabestin.android.wristbarcode.googlewallet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.RsaSHA256Signer;

import org.joda.time.Instant;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.walletobjects.model.DateTime;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.wallet.objects.utils.WobCredentials;
import com.google.wallet.objects.utils.WobPayload;
import com.google.wallet.objects.webservice.WebserviceResponse;

/**
 * This class contains utility functions to create clients for API access and
 * generate JWTs for Save To Wallet and the Webservice API.
 *
 * @author pying
 */
public class WobUtils {

  private final String SAVE_TO_WALLET = "savetowallet";
  private final String LOYALTY_WEB = "loyaltywebservice";
  private final String GOOGLE = "google";

  private final String serviceAccountId;


//  public enum IconType {
//    EMPTY_ICON,
//    EARNINGS,
//    DISCOUNTS,
//    AWARDS,
//    SHIPPING,
//    SOFT_BENEFITS,
//    DISCLAIMER
//  }


  RsaSHA256Signer signer = null;
  Gson gson = new Gson();

  /**
   * Constructer configures the components necessary to sign JWTs and create
   * OAuth tokens for requests
   *
   * @param credentials
   * @throws FileNotFoundException
   * @throws IOException
   * @throws KeyStoreException
   * @throws GeneralSecurityException
   */
  public WobUtils(WobCredentials credentials) throws IOException, GeneralSecurityException {
    serviceAccountId = credentials.getServiceAccountId();
    signer = new RsaSHA256Signer(serviceAccountId, null, credentials.getServiceAccountPrivateKey());
  }

  /**
   * Generates a failure Webservice API failure response JWT.
   *
   * @param resp
   * @return
   * @throws SignatureException
   */
  public String generateWebserviceFailureResponseJwt(WebserviceResponse resp)
      throws SignatureException {
    return generateWebserviceResponseJwt(null, resp);
  }

  /**
   * Generates the linking/signup JWT from a Wallet Object
   *
   * @param object
   * @param resp
   * @return
   * @throws SignatureException
   */
  public String generateWebserviceResponseJwt(GenericJson object,
      WebserviceResponse resp) throws SignatureException {

    JsonToken token = new JsonToken(signer);
    token.setAudience(GOOGLE);
    token.setParam("typ", LOYALTY_WEB);
    token.setIssuedAt(new Instant(
        Calendar.getInstance().getTimeInMillis() - 5000L));
    WobPayload payload = new WobPayload();

    if (object != null) {
      object.setFactory(new GsonFactory());
      payload.addObject(object);
    }

    payload.setResponse(resp);
    JsonObject obj = gson.toJsonTree(payload).getAsJsonObject();
    token.getPayloadAsJsonObject().add("payload", obj);
    return token.serializeAndSign();
  }

  /**
   * Generates the Save to Wallet JWT from a Wallet Object. Useful for when you
   * create Save to Wallet JWTs with a single object.
   *
   * @param object
   * @param origins
   * @return
   * @throws SignatureException
   */
  public String generateSaveJwt(GenericJson object, List<String> origins)
      throws SignatureException {
    WobPayload payload = new WobPayload();
    payload.addObject(object);
    return generateSaveJwt(payload, origins);
  }

  /**
   * Generates the Save to Wallet JWT from a WobPayload. Useful for when you
   * create Save to Wallet JWTs with multiple objects/classes.
   *
   * @param payload
   * @param origins
   * @return
   * @throws SignatureException
   */
  public String generateSaveJwt(WobPayload payload, List<String> origins)
      throws SignatureException {
    JsonToken token = new JsonToken(signer);
    token.setAudience(GOOGLE);
    token.setParam("typ", SAVE_TO_WALLET);
    token.setIssuedAt(new Instant(
        Calendar.getInstance().getTimeInMillis() - 5000L));
    JsonObject obj = gson.toJsonTree(payload).getAsJsonObject();
    token.getPayloadAsJsonObject().add("payload", obj);
    token.getPayloadAsJsonObject().add("origins", gson.toJsonTree(origins));
    return token.serializeAndSign();
  }

  /**
   * Convert RFC3339 String YYYY-MM-DDTHH:MM:SSZ to DateTime object
   *
   * @param rfc3339
   * @return
   */
  public static DateTime toDateTime(String rfc3339) {
    return new DateTime().setDate(com.google.api.client.util.DateTime
        .parseRfc3339(rfc3339));
  }
}
