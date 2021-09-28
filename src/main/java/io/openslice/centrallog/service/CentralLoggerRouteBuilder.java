package io.openslice.centrallog.service;
/*-
 * ========================LICENSE_START=================================
 * io.openslice.portal.api
 * %%
 * Copyright (C) 2019 openslice.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
/**
 * @author ichatzis
 **/

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class CentralLoggerRouteBuilder  extends RouteBuilder{
		
	
	private static String CENTRALLOGGERURL = "";
	
	@Autowired
	private CentralLoggerConfig centrallogerconfig;
	private static final transient Log logger = LogFactory.getLog( CentralLoggerRouteBuilder.class.getName());
	
	public void configure() {

		
		if ( centrallogerconfig.getCentrallogurl() != null) {
			CENTRALLOGGERURL = centrallogerconfig.getCentrallogurl();
		}
		
		if ( ( CENTRALLOGGERURL == null ) || CENTRALLOGGERURL.equals( "" ) ){
			logger.info( "NO CENTRALLOGGERURL ROUTING. ELASTICURL = " + CENTRALLOGGERURL);
			return; 
		}
		logger.info( "ENABLED CENTRALLOGGERURL ROUTING. ELASTICURL = " + CENTRALLOGGERURL);			
	
		String url = CENTRALLOGGERURL;

		from("activemq:queue:centrallogger.log")
		.log( "activemq:queue:centrallogger.log package with body ${body} !" )
		.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http.HttpMethods.POST))
        .setHeader("Content-Type", constant("application/json"))
        .to("log:DEBUG?showBody=true&showHeaders=true")
        .doTry()
			.toD(  url  )
	        .to("log:DEBUG?showBody=true&showHeaders=true")			
        .doCatch(Exception.class)
	        .setBody(exceptionMessage().convertToString())
	        .to("log:DEBUG?showBody=true&showHeaders=true")			
	    .end();
	}
	
}
