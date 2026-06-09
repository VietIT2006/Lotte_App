package com.ptithcm.lottemart.data.remote;

import android.util.Log;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class SocketManager {
    private static Socket mSocket;
    
    // Khởi tạo và trả về đối tượng Socket
    public static Socket getSocket() {
        if (mSocket == null) {
            try {
                // Sửa thành URL backend thực tế hoặc NetworkConfig.BASE_URL
                // Ở đây do BASE_URL có thể chứa "/api" nên ta cần lấy phần host chính
                String socketUrl = NetworkConfig.BASE_URL; 
                if (socketUrl.endsWith("/api/")) {
                    socketUrl = socketUrl.replace("/api/", "");
                } else if (socketUrl.endsWith("/api")) {
                    socketUrl = socketUrl.replace("/api", "");
                }
                
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
