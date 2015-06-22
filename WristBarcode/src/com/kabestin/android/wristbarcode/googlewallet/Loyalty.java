package com.kabestin.android.wristbarcode.googlewallet;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.walletobjects.model.*;
import com.google.wallet.objects.utils.WobUtils;

/**
 * Class to generate example Loyalty class and objects
 *
 * @author pying
 */
public class Loyalty {

  /**
   * Generates a Loyalty Object
   *
   * @param issuerId
   * @param classId
   * @param objectId
   * @return loyaltyObject
   */
  public static LoyaltyObject generateLoyaltyObject(String issuerId,
      String classId, String objectId) {
    // Define Barcode
    Barcode barcode = new Barcode().setType("qrCode")
        .setValue("28343E3")
        .setAlternateText("12345")
        .setLabel("User Id");

    // Define Points
    LoyaltyPoints points = new LoyaltyPoints()
        .setLabel("Points")
        .setPointsType("points")
        .setBalance(new LoyaltyPointsBalance().setString("500"));

    // Define Text Module Data
    List<TextModuleData> textModulesData = new ArrayList<TextModuleData>();
    TextModuleData textModuleData = new TextModuleData()
        .setHeader("Jane's Baconrista Rewards")
        .setBody(
            "Save more at your local Mountain View store Jane.  You get 1 bacon fat latte for every 5 coffees purchased.  Also just for you, 10% off all pastries in the Mountain View store.");
    textModulesData.add(textModuleData);

    // Define Links Module Data
    List<Uri> uris = new ArrayList<Uri>();
    Uri uri1 = new Uri().setDescription("My Baconrista Account")
        .setUri("http://www.baconrista.com/myaccount?id=1234567890");
    uris.add(uri1);
    LinksModuleData linksModuleData = new LinksModuleData().setUris(uris);

    // Define Info Module
    List<LabelValue> row0cols = new ArrayList<LabelValue>();
    LabelValue row0col0 = new LabelValue().setLabel("Next Reward in")
        .setValue("2 coffees");
    LabelValue row0col1 = new LabelValue().setLabel("Member Since")
        .setValue("01/15/2013");
    row0cols.add(row0col0);
    row0cols.add(row0col1);

    List<LabelValue> row1cols = new ArrayList<LabelValue>();
    LabelValue row1col0 = new LabelValue().setLabel("Local Store")
        .setValue("Mountain View");
    row1cols.add(row1col0);

    List<LabelValueRow> rows = new ArrayList<LabelValueRow>();
    LabelValueRow row0 = new LabelValueRow().setHexBackgroundColor("#922635")
        .setHexFontColor("#F8EDC1").setColumns(row0cols);
    LabelValueRow row1 = new LabelValueRow().setHexBackgroundColor("#922635")
        .setHexFontColor("#F8EDC1").setColumns(row1cols);

    rows.add(row0);
    rows.add(row1);

    InfoModuleData infoModuleData = new InfoModuleData().setHexFontColor("#F8EDC1")
        .setHexBackgroundColor("#442905")
        .setShowLastUpdateTime(true)
        .setLabelValueRows(rows);

    // Define general messages
    List<WalletObjectMessage> messages = new ArrayList<WalletObjectMessage>();
    WalletObjectMessage message = new WalletObjectMessage()
        .setHeader("Hi Jane!")
        .setBody("Thanks for joining our program. Show this message to " +
            "our barista for your first free coffee on us!")
        .setImage(
            new Image().setSourceUri(new Uri()
                .setUri("http://farm4.staticflickr.com/3723/11177041115_6e6a3b6f49_o.jpg")))
        .setActionUri(new Uri().setUri("http://baconrista.com"));
    messages.add(message);

    // Define Wallet Instance
    LoyaltyObject object = new LoyaltyObject()
        .setClassId(issuerId + "." + classId).setId(issuerId + "." + objectId)
        .setState("active").setVersion(1L).setBarcode(barcode).setInfoModuleData(infoModuleData)
        .setAccountName("Jane Doe").setTextModulesData(textModulesData)
        .setMessages(messages).setLinksModuleData(linksModuleData)
        .setAccountId("1234567890").setLoyaltyPoints(points);

    return object;
  }

  /**
   * Generates a Loyalty Class
   *
   * @param issuerId
   * @param classId
   * @return loyaltyClass
   */
  public static LoyaltyClass generateLoyaltyClass(String issuerId,
      String classId) {

    // Define rendering templates per view
    List<RenderSpec> renderSpec = new ArrayList<RenderSpec>();

    RenderSpec listRenderSpec = new RenderSpec().setViewName("g_list")
        .setTemplateFamily("1.loyalty_list");
    RenderSpec expandedRenderSpec = new RenderSpec().setViewName("g_expanded")
        .setTemplateFamily("1.loyalty_expanded");

    renderSpec.add(listRenderSpec);
    renderSpec.add(expandedRenderSpec);

    // Define the Image Module Data
    List<ImageModuleData> imageModuleData = new ArrayList<ImageModuleData>();

    ImageModuleData image = new ImageModuleData().setMainImage(
        new Image().setSourceUri(
            new Uri().setUri("http://farm4.staticflickr.com/3738/12440799783_3dc3c20606_b.jpg")));

    imageModuleData.add(image);

    // Define Text Module Data
    List<TextModuleData> textModulesData = new ArrayList<TextModuleData>();

    TextModuleData textModuleData = new TextModuleData().setHeader("Rewards details")
        .setBody(
            "Welcome to Baconrista rewards.  Enjoy your rewards for being a loyal customer.  10 points for ever dollar spent.  Redeem your points for free coffee, bacon and more!");
    textModulesData.add(textModuleData);

    // Define Links Module Data
    List<Uri> uris = new ArrayList<Uri>();
    Uri uri1 = new Uri().setDescription("Nearby Locations").setUri("http://maps.google.com/maps?q=google");
    Uri uri2 = new Uri().setDescription("Call Customer Service").setUri("tel:6505555555");

    uris.add(uri1);
    uris.add(uri2);

    LinksModuleData linksModuleData = new LinksModuleData().setUris(uris);

    // Define Info Module
    InfoModuleData infoModuleData = new InfoModuleData().setHexFontColor("#F8EDC1")
        .setHexBackgroundColor("#442905")
        .setShowLastUpdateTime(true);

    // Define general messages
    List<WalletObjectMessage> messages = new ArrayList<WalletObjectMessage>();
    WalletObjectMessage message = new WalletObjectMessage()
        .setHeader("Welcome to Baconrista")
        .setBody("Featuring our new bacon donuts.")
        .setImage(
            new Image().setSourceUri(new Uri()
                .setUri("http://farm8.staticflickr.com/7302/11177240353_115daa5729_o.jpg")))
        .setActionUri(new Uri().setUri("http://baconrista.com"));
    messages.add(message);

    // Define Geofence locations
    List<LatLongPoint> locations = new ArrayList<LatLongPoint>();
    locations.add(new LatLongPoint().setLatitude(37.422601).setLongitude(
        -122.085286));
    locations.add(new LatLongPoint().setLatitude(37.424354).setLongitude(
        -122.09508869999999));
    locations.add(new LatLongPoint().setLatitude(40.7406578).setLongitude(
        -74.00208940000002));

    // Create class
    LoyaltyClass wobClass = new LoyaltyClass()
        .setId(issuerId + "." + classId)
        .setIssuerName("Baconrista")
        .setProgramName("Baconrista Rewards")
        .setProgramLogo(
            new Image().setSourceUri(new Uri()
                .setUri("http://farm8.staticflickr.com/7340/11177041185_a61a7f2139_o.jpg")))
        .setRewardsTierLabel("Tier").setRewardsTier("Gold")
        .setImageModulesData(imageModuleData)
        .setTextModulesData(textModulesData)
        .setLinksModuleData(linksModuleData)
        .setInfoModuleData(infoModuleData)
        .setAccountNameLabel("Member Name").setAccountIdLabel("Member Id")
        .setRenderSpecs(renderSpec).setMessages(messages)
        .setReviewStatus("underReview").setAllowMultipleUsersPerObject(true)
        .setLocations(locations);

    return wobClass;
  }
}
