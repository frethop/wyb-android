package com.kabestin.android.wristbarcode.view2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Dimension;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kabestin.android.wristbarcode.control.ActivityResultHandler;
import com.kabestin.android.wristbarcode.control.BarcodeListOnClickListener;
import com.kabestin.android.wristbarcode.control.BarcodeListOnLongClickAction;
import com.kabestin.android.wristbarcode.control.DeleteOnClickAction;
import com.kabestin.android.wristbarcode.control.OpeningMenuAboutItem;
import com.kabestin.android.wristbarcode.control.OpeningMenuSettingsItem;
import com.kabestin.android.wristbarcode.control.OptionsItemHandler;
import com.kabestin.android.wristbarcode.control.WBMainActionAdd;
import com.kabestin.android.wristbarcode.control.WBMainBarcodeResult;
import com.kabestin.android.wristbarcode.control.WBService;
import com.kabestin.android.wristbarcode.model.Barcode;
import com.kabestin.android.wristbarcode.model.BarcodeList;
import com.kabestin.android.wristbarcode.model.ViewHolder;
import com.kabestin.android.wristbarcode.model.WBLocation;

public class WBMain extends Activity {
	
	static public Context parent;
	
	HashMap<Integer, Class> optionItemHandlers, activityResultHandlers, dialogCreators;
	
    private final Random rand = new Random();
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
    private final static int REREAD_BARCODE_LIST = 0x21;
    private final static int DISPLAY_BARCODE = 0x22;
    
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
    
	static public LocationManager locationManager; 
    
    Intent barcodeService = null;
    IntentResult scanResult;
    BitMatrix matrix;
    
    SwipeListView slview;
    BarcodeListViewAdapter adapter;
    
    //static public HashMap<String, String> properties;
	static public SharedPreferences prefs;
	
	int barcodeToDisplay;
	
	long millis1, millis2;
	long bstart, bend;
	
	Messenger mService = null;
	final Messenger messenger = new Messenger(new IncomingHandler());
	final ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  We are communicating with our
	        // service through an IDL interface, so get a client-side
	        // representation of that from the raw service object.
	        mService = new Messenger(service);

	        // We want to monitor the service for as long as we are
	        // connected to it.
	        try {
	            Message msg = Message.obtain(null, 9999);
	            msg.replyTo = messenger;
	            mService.send(msg);

	            // Give it some value as an example.
	            msg = Message.obtain(null, 9999, this.hashCode(), 0);
	            mService.send(msg);
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even
	            // do anything with it; we can count on soon being
	            // disconnected (and then reconnected if it can be restarted)
	            // so there is no need to do anything here.
	        }

	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;
	    }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wbmain);
		
		parent = this;
		barcodeList = new BarcodeList();
		barcodeToDisplay = -1;
		
		prefs = getSharedPreferences("wybPrefs", MODE_MULTI_PROCESS);
		
		//properties = (HashMap<String, String>) prefs.getAll();
		//Boolean byLocation = prefs.getBoolean("sortByLocation", false);
		Boolean byLocation = prefs.getBoolean("sortByLocation", false);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 
		readBarcodeFile();
			
		slview = (SwipeListView)findViewById(R.id.barcode_list);
		slview.setSwipeListViewListener(new BarcodeListItemSwipeAction());

		//These are the swipe listview settings. you can change these
		//setting as your requrement
		slview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
		slview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
		slview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		int w = getWindowManager().getDefaultDisplay().getWidth();
		slview.setOffsetLeft(w-120); // left side offset
		slview.setOffsetRight(convertDpToPixel(0f)); // right side offset
		slview.setAnimationTime(50); // animarion time
		slview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress
		
		adapter = new BarcodeListViewAdapter(this);
		slview.setAdapter(adapter);

		
		//*** Set up option, activity, and dialog handlers		
		optionItemHandlers = new HashMap<Integer, Class>();
		optionItemHandlers.put(R.id.action_add, WBMainActionAdd.class);
		optionItemHandlers.put(R.id.action_settings, OpeningMenuSettingsItem.class);
		optionItemHandlers.put(R.id.action_about, OpeningMenuAboutItem.class);
		
		activityResultHandlers = new HashMap<Integer, Class>();
		activityResultHandlers.put(R.id.action_settings, OpeningMenuSettingsItem.class);
		
		/**
		 * Class for interacting with the main interface of the service.
		 */
		barcodeService = new Intent(this, WBService.class);
		startService(barcodeService);
		//bindService(new Intent(this, WBService.class), mConnection, Context.BIND_AUTO_CREATE);

	}
	
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
	
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		savedInstanceState.putSerializable("scanresult", scanResult);
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {   
		  super.onRestoreInstanceState(savedInstanceState);
		  // Restore UI state from the savedInstanceState.
		  // This bundle has also been passed to onCreate.
		  scanResult = (IntentResult)savedInstanceState.getSerializable("scanresult");
	}
	
    public void onStop()
    {
//        SharedPreferences.Editor editor = prefs.edit();
//        Set<String> keyset = properties.keySet();
//        for (String str : keyset) {
//        	editor.putString(str, properties.get(str));
//        }
//        editor.commit();
        
    	super.onStop();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wbmain, menu);
		mHandler = new Handler();
		return true;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	    super.onPrepareDialog(id, dialog);
	    
	    if (id == 0) {
		    LinearLayout container = (LinearLayout) dialog.findViewById(R.id.new_barcode_name_dialog_button_container);
		    Button button = (Button) container.findViewById(R.id.complete_barcode_name_button);
			button.setOnClickListener(new AddBarcodeAction(dialog, scanResult));
    		EditText et = (EditText) dialog.findViewById(R.id.new_barcode_name);
    		et.setText("");
	    }
	}

	
    // Create dialogs as needed.
    protected Dialog onCreateDialog(int directive) 
    {
    	Dialog dialog = new Dialog(this);

    	//  Dialog for obtaining the name information for a new class
    	if (directive == 0) {
    		dialog.setContentView(R.layout.new_barcode_dialog);
    		dialog.setTitle("Name the Barcode");
    	    dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

    	    LinearLayout container = (LinearLayout) dialog.findViewById(R.id.new_barcode_name_dialog_button_container);
    	    Button button = (Button) container.findViewById(R.id.complete_barcode_name_button);
    		button.setOnClickListener(new AddBarcodeAction(dialog, scanResult));
    		button = (Button) container.findViewById(R.id.cancel_barcode_name_button);
    		button.setOnClickListener(new cancelDialogAction(dialog));
    		
    	} else if (directive == R.id.barcode_info) {
    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		dialog.setContentView(R.layout.barcode_display_dialog);
            Window window = dialog.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            Button button = (Button) dialog.findViewById(R.id.barcode_ok_button);
            button.setOnClickListener(new cancelDialogAction(dialog));
    		
            matrix = getMatrix();
    	    int width = matrix.getWidth();
    	    int height = matrix.getHeight();
    	    int[] pixels = new int[width * height];
    	    for (int y = 0; y < height; y++) {
    	        int offset = y * width;
    	        for (int x = 0; x < width; x++) {
    	        	pixels[offset + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
    	        }
    	    }

    	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    	    
    	    ImageView iv = (ImageView)dialog.findViewById(R.id.barcode_view);
    	    iv.setImageBitmap(bitmap);
    		
    	} else if (directive == R.id.barcode_view) {
    		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		dialog.setContentView(R.layout.barcode_display_dialog);
            Window window = dialog.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            Button button = (Button) dialog.findViewById(R.id.barcode_ok_button);
            button.setOnClickListener(new cancelDialogAction(dialog));
    		
    	    int width = matrix.getWidth();
    	    int height = matrix.getHeight();
    	    int[] pixels = new int[width * height + ROWS_PER_MESSAGE*height];
    	    for (int y = 0; y < height; y++) {
    	        int offset = y * width;
    	        byte[] row = encode(matrix, y, 0, 0);
    	        for (int x = 1; x < row.length; x++) {
    	        	for (int b=7; b>=0; b--) {
	    	        	int bit = (row[x] >> b) & 1;
	    	        	pixels[offset + (x-1)*8 + (7-b)] = (bit==1) ? Color.WHITE : Color.BLACK;
    	        	}
    	        }
    	    }

    	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    	    
    	    ImageView iv = (ImageView)dialog.findViewById(R.id.barcode_view);
    	    iv.setImageBitmap(bitmap);
    	
		} else if (directive == R.id.about_content) {	
			dialog.setContentView(R.layout.about);
			dialog.setTitle("WYB version 2.0");
		    dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		}
		
	    return dialog;
    }
	
    // Implement reactions to options selected from the opening menu.
    public boolean onOptionsItemSelected(MenuItem item) {
    	   	
    	// Find the handler in the optionsHandler table and invoke it
    	Class optionsHandler = (Class)optionItemHandlers.get(item.getItemId());
    	OptionsItemHandler option = null;
		try {
			option = (OptionsItemHandler)optionsHandler.newInstance();
    		return option.handleOptionsItem(this);
		} catch (Exception e) {
			option = null;
		}
		adapter.notifyDataSetChanged();
    	       
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Always deregister any Activity-scoped BroadcastReceivers when the Activity is paused
//        if (dataReceiver != null) {
//            unregisterReceiver(dataReceiver);
//            dataReceiver = null;
//        }
    }
    
    private byte[] encode(BitMatrix bm, int aRow, int xOffset, int yOffset) 
    {
        int bytesPerRow = 16;//BARCODE_IMAGE_WIDTH / 8;
        int endian = bytesPerRow-1;
        byte[] imageBytes = new byte[(bytesPerRow * ROWS_PER_MESSAGE) + 3];       
        int sum = 0;
        
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
            		b = (byte) (b + ((bm.get(by*8+bit, aRow+z)?0:1) << bit));  // correct for endianness
            	}
            	
            	// Different endian than Android
            	// Reverse the bytes in a row.
//            	int place = (bytesPerRow*z)+3+endian;
//            	endian--; 
//            	if (endian == -1) endian = bytesPerRow-1;

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
    
    @Override
    protected void onResume() {
        super.onResume();
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
			} catch (com.google.zxing.WriterException e) {
				System.out.println(e.getMessage());
			}

			return matrix;
		} catch (UnsupportedEncodingException e) { 
			System.out.println(e.getMessage());
			return null;
		}
	}
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	  scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    	  if (scanResult != null) {
    		  new WBMainBarcodeResult(this, scanResult).handleResult();
    		  return;
    	  }
    	  
      	// Find the handler in the optionsHandler table and invoke it
      	// Find the handler class in the ActivityResultHandlers table
      	Class activityResultHandler = (Class)activityResultHandlers.get(requestCode);
      	ActivityResultHandler arHandler = null;
  		try {
  			arHandler = (ActivityResultHandler)activityResultHandler.newInstance();
  			arHandler.handleActivityResult(this, intent);
  			barcodeList.sortCodes();
      		adapter.notifyDataSetChanged();
  		} catch (Exception e) {
  			arHandler = null;
  		}

    }
    
    public void setScanResult(IntentResult scanResult)
    {
    	this.scanResult = scanResult;
    }
    
    public BarcodeList getBarcodeList() {
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
    
    public void setBarcodeToDisplay(int bctd)
    {
    	barcodeToDisplay = bctd;
        try {
            Message msg = Message.obtain(null, bctd);
            msg.replyTo = null;
            messenger.send(msg);
        } catch (RemoteException e) {
            restartServiceWithBarcode(bctd);
        }
    }
    
    public void rereadPrefs() 
    {
    	prefs = getSharedPreferences("wybPrefs", MODE_MULTI_PROCESS);
    }
    
    public void restartService()
    {
    	//Editor editor = prefs.edit();
    	//editor.commit();
    	
    	if (barcodeService != null) stopService(barcodeService); 
    	
		barcodeService = new Intent(this, WBService.class);
		startService(barcodeService);
    }
    
    public void restartServiceWithBarcode(int barcodeToDisplay)
    {
    	if (barcodeService != null) stopService(barcodeService); 
    	
    	Bundle bundle = new Bundle();
    	bundle.putInt("barcodeToDisplay", barcodeToDisplay);
		barcodeService = new Intent(this, WBService.class);
		barcodeService.putExtras(bundle);
		startService(barcodeService);
    }
    
    public void readBarcodeFile()
    {
    	FileInputStream file;
		try {
			file = parent.openFileInput("BarcodeList.txt");
			String f = getFilesDir().getAbsolutePath();
			barcodeList.read(file);
			file.close();
//			if (barcodeList.size() == 0) {
//				barcodeList.generateData();
//				saveBarcodeFile();
//			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			barcodeList.generateData();
//			saveBarcodeFile();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			barcodeList.generateData();
//			saveBarcodeFile();
		}
    }
    
    public void saveBarcodeFile()
    {
    	FileOutputStream file;
		try {
			file = parent.openFileOutput("BarcodeList.txt", Context.MODE_PRIVATE);
			String f = getFilesDir().getAbsolutePath();
			barcodeList.write(file);
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void redrawList()
    {
    	adapter.notifyDataSetChanged();
    }
    
    // Send a broadcast to launch the specified application on the connected Pebble
    public void startWatchApp(View view) {
        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
    }

    // Send a broadcast to close the specified application on the connected Pebble
    public void stopWatchApp(View view) {
        PebbleKit.closeAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
    }
    
    public static void requestBarcodeDisplay(int position) {
        PebbleDictionary data = new PebbleDictionary();
        data.addUint8(DISPLAY_BARCODE, (byte) position);
        PebbleKit.sendDataToPebble(parent, PEBBLE_APP_UUID, data);    	
    }

    public static void sendCommand(int command) {
        PebbleDictionary data = new PebbleDictionary();
        data.addUint8(CMD_KEY, (byte) command);
        PebbleKit.sendDataToPebble(parent, PEBBLE_APP_UUID, data);    	
    }
    
    public static void rereadBarcodeList() {
        sendCommand(REREAD_BARCODE_LIST);    	
    }
    public static void vibrateWatch(Context c) {
        PebbleDictionary data = new PebbleDictionary();
        data.addUint8(CMD_KEY, (byte) CMD_UP);
        PebbleKit.sendDataToPebble(c, PEBBLE_APP_UUID, data);
    }
    
	private class AddBarcodeAction implements OnClickListener {
		Dialog parentDialog;
		IntentResult scanResult;

		public AddBarcodeAction(Dialog dialog, IntentResult sResult) {
			parentDialog = dialog;
			scanResult = sResult;
		}

		public void onClick(View v) {
			WBLocation location = new WBLocation();
			
			EditText nameField = (EditText)parentDialog.findViewById(R.id.new_barcode_name);
			String codeName = (String)nameField.getText().toString();
			barcodeList.add(new Barcode(codeName, scanResult, 
					                        location.getLatitude(), location.getLongitude()));
			saveBarcodeFile();
			redrawList();
			restartService();
			
			sendCommand(WBService.REREAD_BARCODE_LIST);
			
			parentDialog.dismiss();
		}
		
	}

	private class cancelDialogAction implements OnClickListener {
		Dialog parentDialog;

		public cancelDialogAction(Dialog dialog) {
			parentDialog = dialog;
		}

		public void onClick(View v) {
			parentDialog.dismiss();
		}
	}
	
    private class BarcodeListItem {
    	
    	String name, formatName, content;
    	
    	public BarcodeListItem (String n, String f, String c)
    	{
    		name = n;
    		formatName = f;
    		content = c;
    	}
    }
    
    private class BarcodeListViewAdapter extends BaseAdapter 
    {
    	private LayoutInflater mInflater;
    	
    	public BarcodeListViewAdapter(Context context) {
    		mInflater = LayoutInflater.from(context);
    	}
    	
    	public int getCount()
    	{
    		return barcodeList.size();
    	}
    	
    	public BarcodeListItem getItem(int i) {
			return new BarcodeListItem(
					barcodeList.get(i).getName(),
					barcodeList.get(i).getFormatName(),
					barcodeList.get(i).getContent()
					);
    	}
    	
    	public long getItemId(int i) {
    		return i;
    	}
    	
    	public View getView(int arg0, View arg1, ViewGroup arg2)
    	{
    		final ViewHolder holder;
    		View view = arg1;
    		
    		if ( (view == null) || (view.getTag() == null) ) {
    			view = mInflater.inflate(R.layout.barcode_list_row, null);
    			holder = new ViewHolder();
    			holder.mPosition = arg0;
    			holder.name = (TextView)view.findViewById(R.id.barcode_name);
    			//holder.name = (EditText)view.findViewById(R.id.barcode_editable_name);
    			holder.info = (TextView)view.findViewById(R.id.barcode_info);
    			holder.containerRow = view;
    		} else {
    			holder = (ViewHolder)view.getTag();
    		}
    		
    		holder.mPosition = arg0;  
    		holder.name.setText(barcodeList.get(arg0).getName());
			if (holder.name.getVisibility() != View.VISIBLE) {
				ViewSwitcher switcher = (ViewSwitcher) view.findViewById(R.id.barcode_name_editor);
			    switcher.showPrevious();
			}	
    		holder.info.setText("Format: "+barcodeList.get(arg0).getFormatName());
    		view.setTag(holder);
    		ImageButton check = (ImageButton) view.findViewById(R.id.barcode_done_button);
    		check.setTag(holder);
    		//view.setOnClickListener(new BarcodeListOnClickListener(WBMain.this, barcodeList.get(arg0)));
    		//view.setOnLongClickListener(new BarcodeListOnLongClickAction(WBMain.this, arg0));
    		
    		return view;
    	}
    	
    }

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
    
	private class BarcodeListItemSwipeAction extends BaseSwipeListViewListener {
		
		boolean swipeStarted = false;
		
         @Override
         public void onOpened(int position, boolean toRight) {
        	 final int pos = position;
             Log.d("swipe", String.format("onOpened %d - toRight %s", position, toRight));   
             slview.closeAnimate(pos);//when you touch back view it will close
             if (swipeStarted) {
            	 swipeStarted = false;
            	 String bn = barcodeList.get(position).getName();
            	 AlertDialog.Builder builder = new AlertDialog.Builder(WBMain.this);
             	 builder
             	 	.setTitle("Delete "+bn)
             	 	.setMessage("Are you sure?")
             	 	.setIcon(android.R.drawable.ic_dialog_alert)
             	 	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             	 		public void onClick(DialogInterface dialog, int which) {			      	
             	 			//Yes button clicked, do something
             	 			(new DeleteOnClickAction(WBMain.this, pos)).onClick(null);
             	 		}
             	 	})
             	 	.setNegativeButton("No", new DialogInterface.OnClickListener() {
             	 		public void onClick(DialogInterface dialog, int which) {			      	
             	 			adapter.notifyDataSetChanged();
             	 		}
             	 	})						
             	 	.show();           	 
             } else {
            	 (new BarcodeListOnLongClickAction(WBMain.this, position))
            	     .onLongClick(slview.getChildAt(position));
             }
         }
 
         @Override
         public void onClosed(int position, boolean fromRight) {
         }
 
         @Override
         public void onListChanged() {
         }
 
         @Override
         public void onMove(int position, float x) {
         }
 
         @Override
         public void onStartOpen(int position, int action, boolean right) {
             Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
             swipeStarted = true;
         }
 
         @Override
         public void onStartClose(int position, boolean right) {
             Log.d("swipe", String.format("onStartClose %d", position));
         }
 
         @Override
         public void onClickFrontView(int position) {
             Log.d("swipe", String.format("onClickFrontView %d", position));
 
     		(new BarcodeListOnClickListener(WBMain.this, barcodeList.get(position)))
     		     .onClick(slview.getChildAt(position));

         }
 
         @Override
         public void onClickBackView(int position) {
             Log.d("swipe", String.format("onClickBackView %d", position));
 
             slview.closeAnimate(position);//when you touch back view it will close
         }
 
         @Override
         public void onDismiss(int[] reverseSortedPositions) {
 
         }
 
	}


}
