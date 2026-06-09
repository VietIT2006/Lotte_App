package com.ptithcm.lottemart.data.api;

import com.ptithcm.lottemart.data.models.NominatimResponse;
import com.ptithcm.lottemart.data.models.OsrmResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MapApiService {
    // Gọi Nominatim để chuyển địa chỉ thành toạ độ
    @Headers("User-Agent: LotteMartApp/1.0")
    @GET
    Call<List<NominatimResponse>> geocodeAddress(
            @Url String url,
            @Query("q") String address,
            @Query("format") String format
    );

    // Gọi OSRM để lấy đường vẽ
    @GET
    Call<OsrmResponse> getRoute(@Url String url);

    // Gọi Nominatim để giải mã toạ độ thành địa chỉ (Reverse Geocoding)
    @Headers("User-Agent: LotteMartApp/1.0")
    @GET
    Call<NominatimResponse> reverseGeocode(
            @Url String url,
            @Query("lat") String latitude,
            @Query("lon") String longitude,
            @Query("format") String format
    );
}
