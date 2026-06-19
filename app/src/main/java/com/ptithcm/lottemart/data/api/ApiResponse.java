package com.ptithcm.lottemart.data.api;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private T data;

    @SerializedName("pagination")
    private PaginationMeta pagination;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public PaginationMeta getPagination() { return pagination; }

    public static class PaginationMeta {
        private int total;
        private int page;
        private int total_pages;
        private int limit;

        public int getTotal() { return total; }
        public int getPage() { return page; }
        public int getTotalPages() { return total_pages; }
        public int getLimit() { return limit; }
    }
}
