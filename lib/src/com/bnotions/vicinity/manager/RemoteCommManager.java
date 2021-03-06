/***********************************************************************************************************************
 *
 * Vicinity - Multi-Screen Android SDK
 * ==========================================
 *
 * Copyright (C) 2012 by Matthew Patience
 * http://www.github.com/MatthewPatience/Vicinity
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package com.bnotions.vicinity.manager;

import java.util.ArrayList;

import android.util.Log;

import com.bnotions.vicinity.device.DeviceAbsImpl;
import com.bnotions.vicinity.device.DeviceListener;
import com.bnotions.vicinity.device.RemoteDevice;
import com.bnotions.vicinity.device.ServerDevice;
import com.bnotions.vicinity.object.VicinityScanResult;
import com.bnotions.vicinity.util.Constants;


public class RemoteCommManager implements DeviceListener {
	
	private ServerDevice server;
	private ArrayList<DeviceListener> list_listeners;
	
	private VicinityScanResult scan_result;
	
	public RemoteCommManager() {
		
		server = new ServerDevice();
		server.setDeviceListener(this);
		
		list_listeners = new ArrayList<DeviceListener>();
		
	}
	
	/**
	 * Returns a reference to the server; is not guaranteed to be connected.
	 * 
	 * @return The server device
	 */
	public ServerDevice getServerDevice() {
		
		return server;
	}
	
	public void connect(VicinityScanResult result) throws Exception {
		
		this.scan_result = result;
		
		server.setIpAddress(this.scan_result.getIpAddress());
		
		if (!server.isConnected()) {
			if (Constants.DEBUG) Log.d("Vicinity", "REMOTEMANAGER - CONNECT ON PORT + " + RemoteDevice.DEFAULT_PORT);
			server.setPort(RemoteDevice.DEFAULT_PORT);
			try {
				server.connect();
			} catch (Exception e) {
				throw new Exception("Error occured while attempting to connect on port " + RemoteDevice.DEFAULT_PORT);
			}
		}
		
	}
	
	/**
	 * Will send a String message to the server.
	 * 
	 * @param message The message to be sent
	 */
	public void sendMessage(String message) {
		
		server.sendMessage(message);
		
	}
	
	/**
	 * Will disconnect the server device and reset it to it's default state.
	 */
	public void disconnect() {
		
		if (Constants.DEBUG) Log.d("Vicinity", "REMOTEMANAGER - DISCONNECT REQUESTED");
		
		if (server.isConnected()) {
			server.disconnect();
		}
		
		server = new ServerDevice();
		server.setDeviceListener(this);
		
	}
	
	/**
	 * Will register an object for updates on device status.
	 * 
	 * @param listener
	 */
	public void addDeviceListener(DeviceListener listener) {
		
		list_listeners.add(listener);
		
	}
	
	public void removeDeviceListener(DeviceListener listener) {
		
		list_listeners.remove(listener);
		
	}

	public void connected(DeviceAbsImpl device) {
		
		if (Constants.DEBUG) Log.d("Vicinity", "REMOTEMANAGER - CONNECTED - " + device.getPort());
		
		int num_listeners = list_listeners.size();
		for (int i = 0; i < num_listeners; i++) {
			DeviceListener listener = list_listeners.get(i);
			listener.connected(device);
		}
		
	}

	public void disconnected(DeviceAbsImpl device) {
		
		if (Constants.DEBUG) Log.d("Vicinity", "REMOTEMANAGER - DISCONNECTED - " + device.getPort());
		
		int num_listeners = list_listeners.size();
		for (int i = 0; i < num_listeners; i++) {
			DeviceListener listener = list_listeners.get(i);
			listener.disconnected(device);
		}
		
	}

	public void messageReceived(DeviceAbsImpl device, String message) {
		
		int num_listeners = list_listeners.size();
		for (int i = 0; i < num_listeners; i++) {
			DeviceListener listener = list_listeners.get(i);
			listener.messageReceived(device, message);
		}
		
	}

}
