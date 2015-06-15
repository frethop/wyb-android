/**
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in
 * compliance with the License.You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kabestin.android.wristbarcode.googlewallet;


/**
 * Static configuration class that handles constants.  I recommend not using
 * this method and storing your keys in a secure area.
 * <p/>
 * This was implemented for convenience's sake.
 *
 */
public class Config {


  /**
   * Available environments
   * <p/>
   * SANDBOX - Test environment where no real world transactions are processed
   * PRODUCTION - Production environment with real credit cards
   */
  enum Environment {
    SANDBOX, PRODUCTION
  }

  // Set the environment that you're deploying against
  private static Environment env = Environment.SANDBOX;

  public static final String MERCHANT_NAME = "MERCHANT_NAME";

  // Credentials
  public static final String SANDBOX_MERCHANT_ID = "SANDBOX_MERCHANT_ID";
  public static final String SANDBOX_MERCHANT_SECRET = "SANDBOX_MERCHANT_SECRET";

  public static final String PROD_MERCHANT_ID = "PROD_KEY_HERE";
  public static final String PROD_MERCHANT_SECRET = "PROD_MERCHANT_SECRET";

  public static final String OAUTH_CLIENT_ID = "1034771164092-hc1ffppjbh8icnrvn5b7uqt26k2vrhp5.apps.googleusercontent.com";

  /**
   * Helper function to check the current environment
   *
   * @return boolean based on if the environment to run against is sandbox
   */
  public static boolean isSandbox() {
    return env.equals(Environment.SANDBOX);
  }

  /**
   * Helper function to get Merchant Id based on the configured environment
   *
   * @return Merchant Id
   */
  public static String getMerchantId() {
    return isSandbox() ? SANDBOX_MERCHANT_ID : PROD_MERCHANT_ID;
  }

  /**
   * Helper function to get Merchant Secret based on the configured environment
   *
   * @return Merchant Secret
   */
  public static String getMerchantSecret() {
    return isSandbox() ? SANDBOX_MERCHANT_SECRET : PROD_MERCHANT_SECRET;
  }

  public static String getMerchantName() {
    return MERCHANT_NAME;
  }

  //Request currency
  public static final String CURRENCY = "USD";

  public static String getOauthClientId() {
    return OAUTH_CLIENT_ID;
  }

  /**
   * Helper function to return the protocol://domain:port
   *
   * @param req servlet request
   * @return request protocol://domain:port
   */
//  public static String getDomain(HttpServletRequest req) {
//    String domain = req.getServerName();
//    String protocol = req.getScheme();
//    String port = Integer.toString(req.getServerPort());
//    String origin = protocol + "://" + domain;
//    if (!(port.equals("80") || port.equals("443"))) {
//      origin += ":" + port;
//    }
//    return origin;
//  }
}
