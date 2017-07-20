package com.early.main;

import java.lang.reflect.Method;
import java.util.List;

import com.early.http.servlet.api.annotation.UrlPath;
import com.early.http.servlet.api.util.UrlMappingScanner;

public class HttpServerTestDriver {
	
	public static void main(String[] args) {
		UrlMappingScanner scanner = UrlMappingScanner.getScanner();
		List<Method> methods = scanner.findMethods(HttpServerTestDriver.class);
		methods.forEach(System.out::println);
		
	}
	
	@UrlPath(value="/hello")
	public String helloServlet() {
		return "<h1>hello</h1>";
	}

}
