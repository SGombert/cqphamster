package de.tudarmstadt.digitalhumanities.cqphamster.api;

import java.io.IOException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;

public class Initializer implements WebApplicationInitializer {
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		Ignition.setClientMode(true);
		
		
		String tempFolder;
		int maxUploadSize;
		try {
			tempFolder = Utils.getConfigurationValue("pathToTemp") + "/uploads";
			
			maxUploadSize = Integer.parseInt(Utils.getConfigurationValue("maxUploadSize"));
		} catch (IOException e) {			
			throw new ServletException(e);
		}

        ServletRegistration.Dynamic appServlet = servletContext.addServlet("mvc", new DispatcherServlet(
                new GenericWebApplicationContext()));
       
              appServlet.setLoadOnStartup(1);
               
              MultipartConfigElement multipartConfigElement = new MultipartConfigElement(tempFolder, 
                maxUploadSize, maxUploadSize * 2, maxUploadSize / 2);
               
              appServlet.setMultipartConfig(multipartConfigElement);		
	}

}
