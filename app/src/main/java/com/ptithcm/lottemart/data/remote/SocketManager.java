package com.ptithcm.lottemart.data.remote;

import android.util.Log;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class SocketManager {
    private static Socket mSocket;
    
    private static String lastSocketUrl = "";

    // Khởi tạo và trả về đối tượng Socket
    public static Socket getSocket() {
        String socketUrl = NetworkConfig.BASE_URL; 
        if (socketUrl.endsWith("/api/v1/")) {
            socketUrl = socketUrl.replace("/api/v1/", "");
        } else if (socketUrl.endsWith("/api/v1")) {
            socketUrl = socketUrl.replace("/api/v1", "");
        } else if (socketUrl.endsWith("/api/")) {
            socketUrl = socketUrl.replace("/api/", "");
        } else if (socketUrl.endsWith("/api")) {
            socketUrl = socketUrl.replace("/api", "");
        }

        if (mSocket == null || !socketUrl.equals(lastSocketUrl)) {
            if (mSocket != null) {
                try {
                    mSocket.disconnect();
                } catch (Exception ignored) {}
            }
            try {
                lastSocketUrl = socketUrl;
                mSocket = IO.socket(socketUrl);
            } catch (URISyntaxException e) {
                Log.e("SocketManager", "URI Error", e);
            }
        }
        return mSocket;
    }

    public static void connect() {
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
        }
    }

    public static void disconnect() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.disconnect();
        }
    }
}
