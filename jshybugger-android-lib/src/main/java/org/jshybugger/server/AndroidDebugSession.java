package org.jshybugger.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import org.jshybugger.DebugContentProvider;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AndroidDebugSession extends DebugSession {

	/** The Constant TAG. */
	private static final String TAG = "DebugServer";
	
	/** The application context. */
	protected Context application;
	
	public final String PROVIDER_PROTOCOL;

	public AndroidDebugSession(Context application ) throws UnknownHostException {
		super(UUID.randomUUID().toString().toUpperCase());
		this.application = application;
		PROVIDER_PROTOCOL = DebugContentProvider.getProviderProtocol(application);
	}
	
	/**
	 * Load script resource by URI.
	 *
	 * @param scriptUri the script URI to load
	 * @param encode true to use base64 encoding
	 * @return the file resource content 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public String loadScriptResourceById(String scriptUri, boolean encode) throws IOException {
		
		Log.d(TAG, "loadScriptResourceById: " + scriptUri);
		
		Cursor cursor = application.getContentResolver().query(Uri.parse(PROVIDER_PROTOCOL + scriptUri), 
				new String[] { encode ? "scriptSourceEncoded" : "scriptSource" }, 
				DebugContentProvider.ORIGNAL_SELECTION, 
				null, 
				null);
		
		String resourceContent=null;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				resourceContent = cursor.getString(0);
			}
			cursor.close();
		}
		
		return resourceContent;
	}

	@Override
	public String setScriptSource(JSONObject message) throws JSONException {
		ContentValues values = new ContentValues();
		values.put("scriptSource", message.getJSONObject("params").getString("scriptSource"));
		Uri uri = Uri.parse(PROVIDER_PROTOCOL + message.getJSONObject("params").getString("scriptId"));
		application.getContentResolver().update(uri, values, null, null);
		return null;

	}
}
