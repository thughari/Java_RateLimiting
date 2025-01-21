package com.thughari.ratelimiting.model;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitInfo {
	
	private AtomicInteger requestCount;
    private long lastRequestTime;
    
	public RateLimitInfo(AtomicInteger requestCount, long lastRequestTime) {
		super();
		this.requestCount = requestCount;
		this.lastRequestTime = lastRequestTime;
	}
	
	public AtomicInteger getRequestCount() {
		return requestCount;
	}
	
	public void setRequestCount(AtomicInteger requestCount) {
		this.requestCount = requestCount;
	}
	
	public long getLastRequestTime() {
		return lastRequestTime;
	}
	
	public void setLastRequestTime(long lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	} 
}
