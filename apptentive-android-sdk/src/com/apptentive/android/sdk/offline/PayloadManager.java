/*
 * Copyright (c) 2011, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.offline;

import android.content.Context;
import android.content.SharedPreferences;
import com.apptentive.android.sdk.GlobalInfo;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.comm.ApptentiveClient;
import com.apptentive.android.sdk.util.Constants;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Sky Kelsey
 */
public class PayloadManager implements Runnable {

	// *************************************************************************************************
	// ********************************************* Static ********************************************
	// *************************************************************************************************

	private static final String PAYLOAD_KEY_PREFIX = "payload-";

	private static PayloadManager instance;
	private static boolean running;

	public static PayloadManager getInstance() {
		if (instance == null) {
			instance = new PayloadManager();
		}
		return instance;
	}


	// *************************************************************************************************
	// ********************************************* Private *******************************************
	// *************************************************************************************************

	private LinkedBlockingQueue<String> queue;
	private SharedPreferences prefs;

	private PayloadManager() {
		this.queue = new LinkedBlockingQueue<String>();
	}

	public void setContext(Context context) {
		prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
	}

	public void start() {
		// When the app starts, clear out the queue and fill it back up. This ensures all payloads get sent
		// upon startup if the network is available.
		instance.queue.clear();
		instance.initQueue();
		instance.ensureRunning();
	}

	public synchronized void ensureRunning() {
		if (!running) {
			running = true;
			new Thread(this).start();
		}
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// LinkedBlockingQueue
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	public void putPayload(Payload payload) {
		String name = PAYLOAD_KEY_PREFIX + UUID.randomUUID().toString();
		prefs.edit().putString(name, payload.getAsJSON()).commit();
		queue.offer(name);
		ensureRunning();
	}

	private synchronized void initQueue() {
		Set<String> keys = prefs.getAll().keySet();
		for (String key : keys) {
			if (key.startsWith(PAYLOAD_KEY_PREFIX)) {
				queue.offer(key);
			}
		}
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// Runnable
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Blocks on the payload queue until a payload becomes available.
	 * If any error occurs, exit thread.
	 * If the json is bad, delete it.
	 * If the json was sent successfully, delete it.
	 * If the json is rejected by the server, delete it.
	 */
	public void run() {
		try {
			while (true) {
				String name = null;
				try {
					name = queue.take();
				} catch (InterruptedException e) {
					break;
				}
				if (name == null) {
					// Can this even happen?
					break;
				}
				Log.v("Got a payload to send: " + name);
				String json = prefs.getString(name, null);
				Log.v("Payload contents: " + json);
				if (json == null) {
					prefs.edit().remove(name).commit();
					continue;
				}
				ApptentiveClient client = new ApptentiveClient(GlobalInfo.apiKey);
				int code = client.postJSON(json);
				if (code >= 200 && code < 300) { // Success
					prefs.edit().remove(name).commit();
				} else if (code >= 400 && code < 500) { // Rejected by server.
					prefs.edit().remove(name).commit();
				} else { // Transient error / overload.
					Log.d("Unable to send JSON. Placing back in queue.");
					// On failures involving the network or server, put the item back on the queue.
					queue.offer(name);
					// Break the loop. Restart when network is reachable.
					break;
				}
			}
		} finally {
			running = false;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

}
