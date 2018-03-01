package com.tigerfixonline.crud;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.tigerfixonline.crud.upload.ImageResource;

@WebListener("App context listener")
public class CrudContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();

		/* Cache */
		ConcurrentLinkedHashMap<String, ImageResource> cache = new ConcurrentLinkedHashMap.Builder<String, ImageResource>()
				.maximumWeightedCapacity(1000).build();
		Lock cacheLock = new ReentrantLock();
		servletContext.setAttribute("cache", cache);
		servletContext.setAttribute("cacheLock", cacheLock);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
