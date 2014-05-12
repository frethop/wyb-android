package com.kabestin.android.wristbarcode.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.google.zxing.common.BitMatrix;

public class MatrixOps {
	
	static private void swap(int i, int j, byte[] arr) {
		  byte t = arr[i];
		  arr[i] = arr[j];
		  arr[j] = t;
	}
	
	//http://stackoverflow.com/questions/2566675/transpose-1d-array-of-leading-dimension-n
	static public byte[] transpose(byte[] matrix, int rows, int columns)
	{
		int i = 0;                /* linear array index */
		int t, jp, kp;
		
		for (int j = 0; j < columns; j++) {       /* j = 1 to columns of non-transposed array */ 
	        for (int k = 0; k < rows; k++) {      /* k = 1 to rows of non-transposed array */ 
	        	i = i + 1;
				t = (k - 1) * columns + j; 
				while (t < i) { 
					jp = (t - 1) % rows + 1; 
				    kp = t - (jp - 1) * rows; 
				    t = (kp - 1) * columns + jp; 
				}
				if (i != t) swap(i, t, matrix);   /* swap elements */
	        }
		}
		
		return matrix;
	}
	
	// http://stackoverflow.com/questions/8422374/java-multi-dimensional-array-transposing
	static public BitMatrix transpose (BitMatrix orig) 
	{
		BitMatrix bm = new BitMatrix(orig.getHeight(), orig.getWidth());
		
		for (int x=0; x<orig.getWidth(); x++) {
			for (int y=0; y<orig.getHeight(); y++) {
				if (orig.get(x, y)) bm.set(y,  x);
			}
		}
		
		return bm;
	}
	
	static public byte[] reverseRows (byte[] matrix, int rows, int columns) 
	{
		for (int r=0; r<rows; r++) {
			for (int c=0; c<columns/2; c++) {
				int start = r*rows;
				int end = r*rows+columns;
				swap(start+c, end-c, matrix);
			}
		}
		
		return matrix;
	}

	static public byte[] reverseColumns (byte[] matrix, int rows, int columns) 
	{
		for (int c=0; c<columns; c++) {
			for (int r=0; r<rows; r++) {
				int start = r*rows;
				int end = r*rows+columns;
				swap(start+c, end-c, matrix);
			}
		}
		
		return matrix;
	}
	
	static public BitMatrix reverseColumns (BitMatrix orig)
	{
		BitMatrix bm = new BitMatrix(orig.getWidth(), orig.getHeight());
		int width = orig.getWidth()-1;
		
		for (int y=0; y<orig.getHeight(); y++) {
			for (int x=0; x<orig.getWidth(); x++) {
				if (orig.get(x, y)) bm.set(width-x, y);
				if (orig.get(width-x, y)) bm.set(x, y);
			}
		}
		
		return bm;
	}
	
	static public BitMatrix reverseRows (BitMatrix orig)
	{
		BitMatrix bm = new BitMatrix(orig.getWidth(), orig.getHeight());
		int height = orig.getHeight()-1;
		
		for (int x=0; x<orig.getWidth(); x++) {
			for (int y=0; y<orig.getHeight(); y++) {
				if (orig.get(x, y)) bm.set(x, height-y);
				if (orig.get(x, height-y)) bm.set(x, y);
			}
		}
		
		return bm;
	}

	
	static public byte[] rotate90clockwise (byte[] matrix, int rows, int columns) 
	{
		return reverseRows(transpose(matrix, rows, columns), rows, columns);
	}

	// not correct yet
	static public byte[] rotate90counterclockwise (byte[] matrix, int rows, int columns) 
	{
		return reverseColumns(transpose(matrix, rows, columns), rows, columns);
	}

	static public byte[] rotate180 (byte[] matrix, int rows, int columns) 
	{
		return
				reverseColumns(
						reverseRows(
								rotate90clockwise(
										rotate90clockwise(matrix, rows, columns),
								rows, columns),
						rows, columns),
				rows, columns);								
	}

	static public BitMatrix rotate90clockwise (BitMatrix matrix) 
	{
		return reverseRows(transpose(matrix));
	}

	// not correct yet
	static public BitMatrix rotate90counterclockwise (BitMatrix matrix) 
	{
		return reverseColumns(transpose(matrix));
	}

	static public BitMatrix rotate180 (BitMatrix matrix) 
	{
		return
				rotate90clockwise(rotate90clockwise(matrix));
	}
	
    private final static int LEFT = 0;
    private final static int TOP = 1;
    private final static int WIDTH = 2;
    private final static int HEIGHT = 3;
	private final static int IMAGE_MARGIN = 25;

    static public BitMatrix resize(BitMatrix bm, int newWidth, int newHeight)
	{
		int[] dims = bm.getEnclosingRectangle();
        System.out.println("RESIZE orig dims [TLWH]: "+dims[TOP]+","+dims[LEFT]+","+dims[WIDTH]+","+dims[HEIGHT]);
		
	    int width = dims[WIDTH];
	    int height = dims[HEIGHT];
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    
	    // create a matrix for the manipulation
	    Matrix matrix = new Matrix();
	    
	    // resize the bit map
	    matrix.postScale(scaleWidth, scaleHeight);
	    
	    // recreate the new Bitmap
	    int[] pixels = new int[width * height];
	    for (int y = 0; y < height; y++) {
	        int offset = y * width;
	        for (int x = 0; x < width; x++) {
	        	pixels[offset + x] = bm.get(x+dims[LEFT], y+dims[TOP]) ? Color.BLACK : Color.WHITE;
	        }
	    }
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

	    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
	    
	    // recreate the new BitMatrix
	    BitMatrix bmm = new BitMatrix(newWidth, newHeight);
	    bmm.clear();
	    for (int y = 0; y < newHeight; y++) {
	    	//if (resizedBitmap.getPixel(0, y) == Color.BLACK) System.out.println("RESIZE black row = "+y);
	        for (int x = 0; x < newWidth; x++) {
	        	if (resizedBitmap.getPixel(x, y) == Color.BLACK) 
	        		bmm.set(x, y);
	        }
	    }
	  
		dims = bmm.getEnclosingRectangle();
        System.out.println("RESIZE resized dims [TLWH]: "+dims[TOP]+","+dims[LEFT]+","+dims[WIDTH]+","+dims[HEIGHT]);

        return bmm;
	}

}
