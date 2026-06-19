package com.ptithcm.lottemart.data.api;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PayOSApiService {
    
    @POST("v2/payment-requests")
    Call<PayOSResponse> createPaymentLink(
            @Header("x-client-id") String clientId,
            @Header("x-api-key") String apiKey,
            @Body PayOSRequest request
    );

    class PayOSRequest {
        public long orderCode;
        public int amount;
        public String description;
        public String cancelUrl;
        public String returnUrl;
        public String signature;
        
        public PayOSRequest(long orderCode, int amount, String description, String cancelUrl, String returnUrl, String signature) {
            this.orderCode = orderCode;
            this.amount = amount;
            this.description = description;
            this.cancelUrl = cancelUrl;
            this.returnUrl = returnUrl;
            this.signature = signature;
        }
    }

    class PayOSResponse {
        @SerializedName("code")
        public String code;
        
        @SerializedName("desc")
        public String desc;
        
        @SerializedName("data")
        public PayOSData data;
    }

    class PayOSData {
        @SerializedName("checkoutUrl")
        public String checkoutUrl;
        
        @SerializedName("paymentLinkId")
        public String paymentLinkId;
        
        @SerializedName("qrCode")
        public String qrCode;
    }
}
