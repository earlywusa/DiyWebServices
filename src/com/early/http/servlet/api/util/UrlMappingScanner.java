package com.early.http.servlet.api.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.early.http.servlet.api.annotation.UrlPath;

public class UrlMappingScanner {
	private static UrlMappingScanner instance = new UrlMappingScanner();
	
	private UrlMappingScanner () {
		
	}
	
	public static UrlMappingScanner getScanner() {
		return instance;
	}
	
	public List<Method> findMethods(Class<?> claz) {
		List<Method> retList = new ArrayList<>();
		Method[] allMethods = claz.getDeclaredMethods();
		for(Method m : allMethods) {
			//System.out.println("current method: " + m);
			Annotation[] annos = m.getDeclaredAnnotations();
			for(Annotation anno : annos) {
				//System.out.println("checking anno: " + anno);
				if(anno.annotationType() == UrlPath.class) {
					retList.add(m);
				}
			}
		}
		return retList;
	}
}
