package com.kabestin.android.wristbarcode.control;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Dimension;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.kabestin.android.wristbarcode.model.Barcode;
import com.kabestin.android.wristbarcode.model.BarcodeList;
import com.kabestin.android.wristbarcode.model.MatrixOps;
import com.kabestin.android.wristbarcode.model.WBLocation;
import com.kabestin.android.wristbarcode.view2.ErrorAlert;
import com.kabestin.android.wristbarcode.view2.R;
import com.kabestin.android.wristbarcode.view2.WBMain;

public class WBService extends Service {
	
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("D8477C50-0205-452A-BB45-30B00606EF8B");
    private final static int CMD_KEY = 0x00;
    private final static int CMD_UP = 0x01;
    private final static int CMD_DOWN = 0x02;
    private final static int REQUEST_BARCODE_LIST_LENGTH = 0x10;
    private final static int SEND_BARCODE_NAMES = 0x11;
    private final static int BARCODE_NAME = 0x12;
    private final static int SEND_BARCODE_NAME = 0x13;
    private final static int SEND_BARCODE = 0x14;
    private final static int BARCODE_IMAGE_BYTES = 0x15;
    private final static int BARCODE_IMAGE = 0x16;
    private final static int BARCODE_IMAGE_DONE = 0x17;
    private final static int BARCODE_NEXT_CHUNK = 0x18;
    private final static int BARCODE_FORMAT = 0x19;
    private final static int BARCODE_ROWS_PER_MESSAGE = 0x20;
    public final static int REREAD_BARCODE_LIST = 0x21;
    public final static int DISPLAY_BARCODE = 0x22;
    private final static int BARCODE_ERROR = 0xFF;
    
    private final static int ROWS_PER_MESSAGE = 5;
    private final static int BARCODE_IMAGE_HEIGHT = 168;
    private final static int BARCODE_IMAGE_WIDTH = 128;
    private final static int ROW_TO_STOP = 168;
    
    private final static int LEFT = 0;
    private final static int TOP = 1;
    private final static int WIDTH = 2;
    private final static int HEIGHT = 3;
	
    private PebbleKit.PebbleDataReceiver dataReceiver;
    private Handler mHandler;
    
    private BarcodeList barcodeList;
    BitMatrix matrix;
    
	int barcodeToDisplay;
	long millis1, millis2;
	long bstart, bend;
	
	Context parent;
    NotificationManager mNM; 
    Messenger messenger;
    
    static public HashMap<String, String> properties;
	SharedPreferences prefs;
    
    String errorMessage;

	public void onCreate() 
	{
		parent = (Context)this;
		barcodeToDisplay = -1;
        messenger = new Messenger(new BarcodeToDisplayHandler());
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);        
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		WBLocation location = null;
		
		prefs = getSharedPreferences("wybPrefs", MODE_MULTI_PROCESS);
		Boolean byLocation = prefs.getBoolean("sortByLocation", false);
		////System.out.println("In SERVICE: byLocation = "+byLocation);
		if (byLocation) {
			////System.out.println("In SERVICE: byLocation set");
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			location = new WBLocation(locationManager);
		}
        
		barcodeList = new BarcodeList(location); 
		readBarcodeFile();
		Barcode bcode = null;
		if (intent != null) {
			barcodeToDisplay = intent.getIntExtra("barcodeToDisplay", -1);
			if (barcodeToDisplay != -1) bcode = barcodeList.get(barcodeToDisplay); 
		}
		barcodeList.sortCodes(byLocation);
		if (barcodeToDisplay != -1) barcodeToDisplay = barcodeList.indexOf(bcode);
		
		mHandler = new Handler();

		dataReceiver = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
			@Override
			public void receiveData(final Context context,
					final int transactionId, final PebbleDictionary data) {
				final int barcodeNumber, cmd, version;
				BitMatrix bm = null;

				// Possible commands
				Long cmdL = data.getUnsignedIntegerAsLong(CMD_KEY);
				if (cmdL != null) {
					barcodeNumber = 0;
					cmd = cmdL.intValue();
					version = 0;
				} else {
					cmdL = data.getUnsignedIntegerAsLong(REQUEST_BARCODE_LIST_LENGTH);
					if (cmdL != null) {
						version = cmdL.intValue();
						cmd = REQUEST_BARCODE_LIST_LENGTH;
						barcodeNumber = 0;
					} else {
						cmdL = data.getUnsignedIntegerAsLong(SEND_BARCODE_NAME);
						if (cmdL != null) {
							barcodeNumber = cmdL.intValue();
							cmd = SEND_BARCODE_NAME;
							version = 0;
						} else {
							cmdL = data.getUnsignedIntegerAsLong(SEND_BARCODE);
							if (cmdL != null) {
								barcodeNumber = cmdL.intValue() == 255 ? barcodeToDisplay
										: cmdL.intValue();
								cmd = SEND_BARCODE;
								version = 0;
							} else {
								cmdL = data.getUnsignedIntegerAsLong(BARCODE_NEXT_CHUNK);
								if (cmdL != null) {
									barcodeNumber = cmdL.intValue() == 255 ? barcodeToDisplay
											: cmdL.intValue();
									cmd = BARCODE_NEXT_CHUNK;
									version = 0;
								} else {
									cmd = 99;
									barcodeNumber = 99;
									version = 0;
								}
							}
						}
					}
				}
				
				System.out.println("Received msg: "+cmd+"/"+barcodeNumber+"/"+version);

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						ArrayList<Barcode> bcl;
						Barcode bcode;
						PebbleDictionary data;
						BitMatrix bm = null;
						byte[] imageMessageBytes;
						int[] dims = { 0, 0, 0, 0 };
						boolean byLocation;

						// All data received from the Pebble must be ACK'd,
						// otherwise you'll hit time-outs in the
						// watch-app which will cause the watch to feel "laggy"
						// during periods of frequent
						// communication.
						PebbleKit.sendAckToPebble(context, transactionId);

						switch (cmd) {
						// send SMS when the up button is pressed
						case REQUEST_BARCODE_LIST_LENGTH:
							if (version >= 10) {
								bcl = ((WBService) context).getBarcodeList();
								data = new PebbleDictionary();
								int sz = (barcodeToDisplay == -1) ? bcl.size()
										: 255;
								data.addUint8(REQUEST_BARCODE_LIST_LENGTH,
										(byte) sz);
								PebbleKit.sendDataToPebble(parent,
										PEBBLE_APP_UUID, data);
							} else {
								new ErrorAlert(parent)
										.showErrorDialog("Version",
												"Please update watch app to at least version 1.0");
							}
							break;
						case SEND_BARCODE_NAMES:
							data = new PebbleDictionary();
							bcl = ((WBService) context).getBarcodeList();
							byLocation = properties==null?false:properties.get("sortByLocation").equals("yes");
							barcodeList.sortCodes(byLocation);

							for (int i = 0; i < bcl.size(); i++) {
								data.addString(BARCODE_NAME, bcl.get(i)
										.getName());
							}
							PebbleKit.sendDataToPebble(parent, PEBBLE_APP_UUID,
									data);
							break;
							
						case SEND_BARCODE_NAME:
							data = new PebbleDictionary();
							bcl = ((WBService) context).getBarcodeList();
							data.addString(BARCODE_NAME, bcl.get(barcodeNumber)
									.getName());
							data.addString(BARCODE_FORMAT,
									bcl.get(barcodeNumber).getFormatName());
							////System.out.println("Sending..."+bcl.get(barcodeNumber).getName());
							PebbleKit.sendDataToPebble(parent, PEBBLE_APP_UUID,
									data);
							break;

						case SEND_BARCODE:
							data = new PebbleDictionary();
							bcl = ((WBService) context).getBarcodeList();
							bcode = bcl
									.get(barcodeNumber == -1 ? getBarcodeToDisplay()
											: barcodeNumber);
							barcodeToDisplay = -1;
							
							System.out.println("SENDING "+bcode.getName());

							// generate the barcode
							System.out.println("format: "+bcode.getFormatName()+", data = "+bcode.getContent());
							bm = generateBarcode(bcode.getContent(),
									bcode.getFormat(),
									(int) (BARCODE_IMAGE_HEIGHT),
									(int) (BARCODE_IMAGE_WIDTH));

							//int diff = Barcode.difference("Top", bcode.getDesiredOrientation());
							int diff = Barcode.difference(bcode.getStartingOrientation(), bcode.getDesiredOrientation());
							if (bm != null) {
								dims = bm.getEnclosingRectangle();
								System.out.println("BEFORE Dims [TLWH]: "
										+ dims[TOP] + "," + dims[LEFT] + ","
										+ dims[WIDTH] + "," + dims[HEIGHT]);
								System.out.println("Rotate Difference = "+diff);
								
								// rotate?
								if (diff == 1)
									bm = MatrixOps.rotate90clockwise(bm);
								else if (diff == -1 || diff == 3)
									bm = MatrixOps.rotate90counterclockwise(bm);
								else if (diff == 2 || diff == -2)
									bm = MatrixOps.rotate180(bm);

								dims = bm.getEnclosingRectangle();
								System.out.println("AFTER ROTATE Dims [TLWH]: "
										+ dims[TOP] + "," + dims[LEFT] + ","
										+ dims[WIDTH] + "," + dims[HEIGHT]);

								// Resize?
									if (dims[WIDTH]>BARCODE_IMAGE_WIDTH || dims[HEIGHT]>BARCODE_IMAGE_HEIGHT) {
										int w = dims[WIDTH];
										int h = dims[HEIGHT];
										float ratio = (float)0.0;
										if (dims[WIDTH]>BARCODE_IMAGE_WIDTH) {
											ratio = BARCODE_IMAGE_WIDTH / dims[WIDTH];
											h = (int)(h * ratio);
											w = (int)(w * ratio);
										}
										if (h>BARCODE_IMAGE_HEIGHT) {
											ratio = BARCODE_IMAGE_HEIGHT / dims[WIDTH];
											h = (int)(h * ratio);
											w = (int)(w * ratio);
										}
										bm = MatrixOps.resize(bm,
												w, h);
										dims = bm.getEnclosingRectangle();
										System.out.println("AFTER RESIZE Dims [TLWH]: "
												+ dims[TOP] + "," + dims[LEFT] + ","
												+ dims[WIDTH] + "," + dims[HEIGHT]);
									}


							}
							
							// Flag error if necessary
							if (bm == null) {
								//Toast.makeText(getApplicationContext(),
								//		"ERROR: "+errorMessage,
								//		Toast.LENGTH_LONG).show();
								System.out.println("Sending ERROR message.");
								data.addString(BARCODE_ERROR, errorMessage);
								bstart = new Date().getTime();
								millis1 = new Date().getTime();
								PebbleKit.sendDataToPebble(parent, PEBBLE_APP_UUID, data);
								return;
							}

							// Record the barcode
							setMatrix(bm);
							dims = bm.getEnclosingRectangle();
							System.out.println("SENDING Dims [TLWH]: "
									+ dims[TOP] + "," + dims[LEFT] + ","
									+ dims[WIDTH] + "," + dims[HEIGHT]);

							// send starting row information
							int firstRow = (dims[TOP] - ROWS_PER_MESSAGE - 2);
							if (firstRow < 0)
								firstRow = -ROWS_PER_MESSAGE;
							firstRow = -5;
							System.out.println("Sending first row number: " + firstRow);
							data.addUint16(BARCODE_IMAGE_BYTES,(short) firstRow);
							bstart = new Date().getTime();
							millis1 = new Date().getTime();
							PebbleKit.sendDataToPebble(parent, PEBBLE_APP_UUID,
									data);
							// showDialog(R.id.barcode_info);

							break;

						case BARCODE_NEXT_CHUNK:
							millis2 = new Date().getTime();
							//System.out.println("NEXT: " + (millis2 - millis1));
							bcl = ((WBService) context).getBarcodeList();
							bm = getMatrix();
							dims = bm.getEnclosingRectangle();

							int nextRow = barcodeNumber + ROWS_PER_MESSAGE;
							while (nextRow < ROW_TO_STOP
									&& nextRow < dims[TOP] + dims[HEIGHT]) {
								//System.out.println(nextRow);
//								if (nextRow < (dims[TOP] - ROWS_PER_MESSAGE)) {
//									nextRow += 1;
//									continue;
//								}

								if (nextRow >= ROW_TO_STOP
										|| nextRow >= dims[TOP] + dims[HEIGHT]) {
									barcodeToDisplay = -1;
									data = new PebbleDictionary();
									data.addInt8(BARCODE_IMAGE_DONE, (byte) 0);
									PebbleKit.sendDataToPebble(parent,
											PEBBLE_APP_UUID, data);
									bend = new Date().getTime();
									//System.out
									//		.println("Total time for barcode: "
									//				+ (bend - bstart));
								} else {
									// send image in pieces, X rows at a time
									imageMessageBytes = encode(bm, nextRow, dims[LEFT], dims[TOP]);
									if (imageMessageBytes[0] == -1) {
										nextRow += 1;
									} else {
										data = new PebbleDictionary();
										data.addBytes(BARCODE_IMAGE,
												imageMessageBytes);
										millis1 = new Date().getTime();
										PebbleKit.sendDataToPebble(parent,
												PEBBLE_APP_UUID, data);
										// Toast.makeText(getApplicationContext(),
										// "Sending row "+nextRow,
										// Toast.LENGTH_LONG).show();
										break;
									}
								}
							}

							if (nextRow >= ROW_TO_STOP
									|| nextRow >= dims[TOP] + dims[HEIGHT]) {
								barcodeToDisplay = -1;
								data = new PebbleDictionary();
								data.addInt8(BARCODE_IMAGE_DONE, (byte) 0);
								PebbleKit.sendDataToPebble(parent,
										PEBBLE_APP_UUID, data);
								bend = new Date().getTime();
								//System.out.println("Total time for barcode: "
								//		+ (bend - bstart));
							}

							break;
						default:
							//Toast.makeText(getApplicationContext(),
							//		"Got something else: " + cmd,
							//		Toast.LENGTH_LONG).show();
							break;
						}
					}
				});
			}
		};
		try {
			PebbleKit.registerPebbleDisconnectedReceiver(this, dataReceiver);
			//this.unregisterReceiver(dataReceiver);
			PebbleKit.registerReceivedDataHandler(this, dataReceiver);
		} catch (Exception e) {
			//PebbleKit.registerPebbleDisconnectedReceiver(this, dataReceiver);
			//this.unregisterReceiver(dataReceiver);
			PebbleKit.registerReceivedDataHandler(this, dataReceiver);
		}
		notify("WYB Service started");

		return (START_STICKY);

	}
	
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(999);
        unregisterReceiver(dataReceiver);
    }

	
	 @Override
	  public IBinder onBind(Intent intent) {
	  //TODO for communication return IBinder implementation
	    return messenger.getBinder();
	  }
	 
	 /**
	  * Handler of incoming messages from service.
	  */
	 class BarcodeToDisplayHandler extends Handler {
	     @Override
	     public void handleMessage(Message msg) {
	         switch (msg.what) {
	             case 9999:
	                 break;
	             default:
	            	 barcodeToDisplay = msg.what;
	            	 //notify("Received "+msg.what);
	                 super.handleMessage(msg);
	         }
	     }
	 }

	 
	public void notify(String msg)
	{
        Notification notification = new Notification (R.drawable.wyb_icon, msg, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, WBMain.class), 0);
		notification.setLatestEventInfo (this, "Barcode Service", msg, contentIntent);
		mNM.notify (999, notification);
	}
	  
    private byte[] encode(BitMatrix bm, int aRow, int xOffset, int yOffset) 
    {
        int byteOffset = xOffset/8;
        int bitOffset = xOffset - byteOffset*8;
        int bytesPerRow = BARCODE_IMAGE_WIDTH / 8;  // 16???
        int endian = bytesPerRow-1;
        byte[] imageBytes = new byte[(bytesPerRow * ROWS_PER_MESSAGE) + 3];       
        int sum = 0;
        
        System.out.println("ENCODING row "+aRow+", offset = ("+xOffset+","+yOffset+"), bOffset = ("+byteOffset+", "+bitOffset+")");
        
        for (int z=0; z < ROWS_PER_MESSAGE; z++) { 
        	if (aRow+z+yOffset >= bm.getHeight()) break;
        	
        	// row # as first 2 elements of data
            if (z == 0) {
            	imageBytes[0] = (byte)(aRow & 0xFF);  // put the row # as the first element
            	imageBytes[1] = (byte)(aRow >> 8 & 0xFF);
            	imageBytes[2] = (byte)bytesPerRow;
            }
            
            sum = 0;
        	for (int by=0; by<bytesPerRow; by++) {
        		
        		// Gather the bits into bytes
        		byte b = 0;  // start out white
            	for (int bit=0; bit<8; bit++) {
            		//b = (byte)(b << 1);
            		b = (byte) (b + ((bm.get((by+byteOffset)*8+bit+bitOffset, aRow+z+yOffset)?0:1) << bit));  // correct for endianness
            	}            	
            	
            	// Place the byte into the correct place in the array
            	int place = (bytesPerRow*z)+by+3;            	
            	imageBytes[place] = b;
            	sum += b;
            	
        	}
        	
        	if (sum == bytesPerRow * -1) {
        		if (z == 0) {
            		imageBytes[0] = -1;
            		return imageBytes;
        		} 
        	}

        }    
    	return imageBytes;
    }
    
	public BitMatrix generateBarcode(String stringData, BarcodeFormat format, int aWidth, int aHeight) {

		Charset charset = Charset.forName("ISO-8859-1");
		CharsetEncoder encoder = charset.newEncoder();
		byte[] b = null;
		try {
			ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(stringData));
			b = bbuf.array();
		} catch (CharacterCodingException e) {
			System.out.println(e.getMessage());
		}

		String data;
		try {
			data = new String(b, "ISO-8859-1");
			// get a byte matrix for the data
			matrix = null;
			com.google.zxing.Writer writer = new MultiFormatWriter();
			try {
				Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>(3);
				hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
				hints.put(EncodeHintType.MARGIN, (Integer)0);
				hints.put(EncodeHintType.MIN_SIZE, new Dimension(aWidth, aHeight));
				matrix = writer.encode(data, format, aWidth, aHeight, hints);
			} catch (Exception e) {
				System.out.println("matrix: "+e.getMessage());
				matrix = null;
				errorMessage = e.getMessage();
			}

			return matrix;
		} catch (UnsupportedEncodingException e) { 
			System.out.println(e.getMessage());
			errorMessage = e.getMessage();
			return null;
		}
	}
	    public ArrayList<Barcode> getBarcodeList() {
	    	return barcodeList;
	    }
	       
	    public void setMatrix (BitMatrix bm)
	    {
	    	matrix = bm;
	    	
	    	//showDialog(R.id.barcode_info);
	    }
	    
	    public BitMatrix getMatrix ()
	    {
	    	return matrix;
	    }
	    
	    public int getBarcodeToDisplay() 
	    {
	    	return barcodeToDisplay;
	    }
	    
//	    public void setBarcodeToDisplay(int bctd)
//	    {
//	    	barcodeToDisplay = bctd;
//            try {
//                Message msg = Message.obtain(null, bctd);
//                msg.replyTo = null;
//                messenger.send(msg);
//            } catch (RemoteException e) {
//                // There is nothing special we need to do if the service
//                // has crashed.
//            }
//
//	    }
	    
	    public void readBarcodeFile()
	    {
	    	FileInputStream file;
			try {
				file = openFileInput("BarcodeList.txt");
				String f = getFilesDir().getAbsolutePath();
				barcodeList.read(file);
				file.close();
//				if (barcodeList.size() == 0) {
//					barcodeList.generateData();
//					saveBarcodeFile();
//				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
//				barcodeList.generateData();
//				saveBarcodeFile();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				barcodeList.generateData();
//				saveBarcodeFile();
			}
	    }
	    

}
