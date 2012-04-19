package com.pakiet2.namespace;

import java.io.InputStream;

import android.content.Context;


public class Utils {
	public static InputStream openFileFromAssets(String spath, Context mycontext) {
		try {
			InputStream is = mycontext.getResources().getAssets().open(spath);
			return is;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String getContentType(String fileName) {
	    if (fileName.endsWith(".htm") || fileName.endsWith(".html") || fileName.endsWith(".txt")) {
	    		return "text/html";
	    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
	    		return "image/jpeg";
	    } else if (fileName.endsWith(".gif")) {
	    		return "image/gif";
	    } else if (fileName.endsWith(".png")) {
		      	return "image/png";
	    } else if (fileName.endsWith(".css")) {
		      	return "text/css";
	    } else {
	    		return "application/octet-stream";
	    }
	}
}