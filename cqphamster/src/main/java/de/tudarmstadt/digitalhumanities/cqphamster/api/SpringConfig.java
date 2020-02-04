package de.tudarmstadt.digitalhumanities.cqphamster.api;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;

//@Configuration
public class SpringConfig {

	//@Bean(name="multipartResolver")
	public CommonsMultipartResolver multipartResolver() throws NumberFormatException, IOException {
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	    multipartResolver.setMaxUploadSize(Integer.parseInt(Utils.getConfigurationValue("maxUploadSize")));
	    return multipartResolver;
	}	
}
