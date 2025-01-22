package com.thughari.ratelimiting.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.thughari.ratelimiting.model.RateLimitInfo;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter implements Filter {
	
	private final Map<String, RateLimitInfo> requestsPerIp = new ConcurrentHashMap<String, RateLimitInfo>();
	
	private static final int MAX_REQUESTS_PER_MINUTE = 5;
	private static final long ONE_MINUTE_IN_MILLIS = 60000;
	
	Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String clientIp = getClientIp(httpServletRequest);
		// String clientIp = httpServletRequest.getRemoteAddr();
		
		requestsPerIp.putIfAbsent(clientIp, new RateLimitInfo(new AtomicInteger(0), System.currentTimeMillis()));
		
//		AtomicInteger requestCount = requestsPerIp.get(clientIp).getRequestCount();

		RateLimitInfo rateLimitInfo = requestsPerIp.get(clientIp);
		
		logger.log(Level.INFO, clientIp);
		
		synchronized (rateLimitInfo) {
//			int requests = requestCount.incrementAndGet();
			long currentTime = System.currentTimeMillis();
			if (currentTime - rateLimitInfo.getLastRequestTime() >= ONE_MINUTE_IN_MILLIS) {
	            rateLimitInfo.getRequestCount().set(0);
	            rateLimitInfo.setLastRequestTime(currentTime);
	        }
			int requests = rateLimitInfo.getRequestCount().incrementAndGet();
			if(requests >MAX_REQUESTS_PER_MINUTE) {
				httpServletResponse.setStatus(429);
				httpServletResponse.getWriter().write("Too many requests. Please try again later.");
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

}
