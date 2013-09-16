package org.jshybugger.server;

import java.io.IOException;

import org.jshybugger.DebugContentProvider;
import org.jshybugger.proxy.ScriptSourceProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AndroidSourceProvider implements ScriptSourceProvider {

	/** The Constant TAG. */
	private static final String TAG = "AndroidSourceProvider";

	private final Context application;
	private final String PROVIDER_PROTOCOL;
	
	public AndroidSourceProvider(Context application) {
		this.application = application;
		PROVIDER_PROTOCOL = DebugContentProvider.getProviderProtocol(application);
	}
	@Override
	public String loadScriptResourceById(String scriptUri, boolean encode)
			throws IOException {
		
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
	public String setScriptSource(String scriptUri, String scriptSource) {
		ContentValues values = new ContentValues();
		values.put("scriptSource",scriptSource);
		Uri uri = Uri.parse(PROVIDER_PROTOCOL + scriptUri);
		application.getContentResolver().update(uri, values, null, null);
		return null;
	}

}
