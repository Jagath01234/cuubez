/**
 *	Copyright [2013] [www.cuubez.com]
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package com.cuubez.client;

import java.security.Security;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.cuubez.client.context.KeyContext;
import com.cuubez.client.context.MessageContext;
import com.cuubez.client.context.RequestContext;
import com.cuubez.client.engine.processor.Processor;
import com.cuubez.client.engine.processor.RequestProcessor;
import com.cuubez.client.exception.CuubezException;
import com.cuubez.client.param.HttpMethod;
import com.cuubez.client.param.MediaType;
import com.cuubez.key.KeyExAlgorithmName;
import com.cuubez.key.exchange.ecdh.ECDHOriginator;
import com.cuubez.key.exchange.ecmqv.ECMQVOriginator;

public class ClientRequest {
	private static Log log = LogFactory.getLog(ClientRequest.class);	
	
	private String serviceUrl;
	private MediaType mediaType;
	private Object[] parameters;
	private String principal;
	private String credentials;
	private String algoName;
	private KeyContext keyContext;
	
	public ClientRequest() {
	}
	
	public ClientRequest(String serviceUrl, MediaType mediaType) {
		this.serviceUrl = serviceUrl;
		this.mediaType = mediaType;
	}
	
	public void addParameters(Object... parameters) {
		this.parameters = parameters;
	}
	
	public void get() throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.GET);
		Processor processor = new RequestProcessor();
		processor.process(msgContext);
	}
	
	public void exchangeKey(String AlgoName) throws CuubezException {
		Security.addProvider(new BouncyCastleProvider());
		
		algoName = AlgoName;
		
		
		if(algoName == "ECMQVALGO")
		
		{
		
		//generate key
		MessageContext msgContext = getKeyContext(HttpMethod.GET);
		
		//send public key
		Processor processor = new RequestProcessor();
		processor.process(msgContext);
		
		if(msgContext.getKeyContext() != null) {
			msgContext.getKeyContext().setSharedSecretKey(
					msgContext
							.getKeyContext()
							.getEcmvqOriginator()
							.originatorSharedSecret(
									msgContext.getKeyContext()
											.getServerPublicKey(), 
									msgContext.getKeyContext()
											.getServerPublicKey2()));
			this.keyContext = msgContext.getKeyContext();
		}
		
		}
		
		else if (algoName == "ECDHALGO" || algoName == "DHALGO" )
		
		{
			//generate key
			MessageContext msgContext = getKeyContext(HttpMethod.GET);
			
			//send public key
			Processor processor = new RequestProcessor();
			processor.process(msgContext);
			
			if(msgContext.getKeyContext() != null) {
				msgContext.getKeyContext().setSharedSecretKey(
						msgContext
								.getKeyContext()
								.getIdhOriginator()
								.originatorSharedSecret(
										msgContext.getKeyContext()
												.getServerPublicKey()));
				this.keyContext = msgContext.getKeyContext();
			}
			
		}
		else
		{
			System.out.println("Provided Key Exchange Algo INVALID");
		}
	}
	
	public <T> T get(Class<T> returnType) throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.GET);
		Processor processor = new RequestProcessor();
		return processor.process(msgContext,returnType);
	}
	
	public void post() throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.POST);
		Processor processor = new RequestProcessor();
		processor.process(msgContext);
	}
	
	public <T> T post(Class<T> returnType) throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.POST);
		Processor processor = new RequestProcessor();
		processor.process(msgContext);
		return processor.process(msgContext, returnType);
	}

	public void delete() throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.DELETE);
		Processor processor = new RequestProcessor();
		processor.process(msgContext);
		processor.process(msgContext);
	}
	
	public <T> T delete(Class<T> returnType) throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.DELETE);
		Processor processor = new RequestProcessor();
		processor.process(msgContext);
		return processor.process(msgContext, returnType);
	}
	
	public void put() throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.PUT);
		Processor processor = new RequestProcessor();
		processor.process(msgContext);
	}
	
	public <T> T put(Class<T> returnType) throws CuubezException {
		validateContent();
		MessageContext msgContext = getMessageContext(HttpMethod.PUT);
		Processor processor = new RequestProcessor();
		return processor.process(msgContext, returnType);
	}
	
	
	private MessageContext getMessageContext(HttpMethod httpMethod) {
		MessageContext msgContext = new MessageContext();
		RequestContext requestContext = new RequestContext(serviceUrl, mediaType, httpMethod);
		requestContext.setParameters(parameters);
		requestContext.setPrincipal(principal);
		requestContext.setCredentials(credentials);
		msgContext.setRequestContext(requestContext);
		return msgContext;
	}
	
	private MessageContext getKeyContext(HttpMethod httpMethod) {
		MessageContext msgContext = new MessageContext();
		RequestContext requestContext = new RequestContext(serviceUrl, mediaType, httpMethod);		
		requestContext.setParameters(parameters);
	if(algoName == "ECMQVALGO")
	{
		
		requestContext.setKeyExAlgoName(KeyExAlgorithmName.ECMQVALGO.getNumVal());
		ECMQVOriginator ecmvqOriginator = new ECMQVOriginator();
		Map<String, byte[]> clientPublicKeys = ecmvqOriginator.generateKeyAgreement();
		
		if(msgContext.getKeyContext() == null){
			msgContext.setKeyContext(new KeyContext());			
		}
		msgContext.getKeyContext().setClientPublicKey(clientPublicKeys.get("key1"));
		msgContext.getKeyContext().setClientPublicKey2(clientPublicKeys.get("key2"));
		msgContext.getKeyContext().setEcmvqOriginator(ecmvqOriginator);
		
		requestContext.setPublicKey(clientPublicKeys.get("key1"));
		requestContext.setPublicKey2(clientPublicKeys.get("key2"));
		msgContext.setRequestContext(requestContext);
		return msgContext;
	}
	else if (algoName == "ECDHALGO" || algoName == "DHALGO" )
	{
		//if (algoName == "ECDHALGO"){
			requestContext.setKeyExAlgoName(KeyExAlgorithmName.ECDHALGO.getNumVal());
		//}
		//else
		//{
		//	requestContext.setKeyExAlgoName(KeyExAlgorithmName.DHALGO.getNumVal());
		//}
		
		ECDHOriginator dhOriginator = new ECDHOriginator();
		byte[] clientPublicKey = dhOriginator.generateKeyAgreement();
		
		if(msgContext.getKeyContext() == null){
			msgContext.setKeyContext(new KeyContext());			
		}
		msgContext.getKeyContext().setClientPublicKey(clientPublicKey);
		msgContext.getKeyContext().setIdhOriginator(dhOriginator);
		
		requestContext.setPublicKey(clientPublicKey);
		msgContext.setRequestContext(requestContext);
		return msgContext;
	}
	else
	{
		System.out.println("Provided Key Exchange Algo is INVALID");
	}
		return msgContext;
	}
	
	private void validateContent() throws CuubezException {
		
		if(serviceUrl == null || mediaType == null) {
			log.error("ServiceURL and MediaType cant be a null");
			throw new CuubezException();
		}
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public Object[] getParameters() {
		return parameters;
	}

    /**
     * @return the principal
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * @param principal the principal to set
     */
    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    /**
     * @return the credentials
     */
    public String getCredentials() {
        return credentials;
    }

    /**
     * @param credentials the credentials to set
     */
    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

	/**
	 * @return the keyContext
	 */
	public KeyContext getKeyContext() {
		return keyContext;
	}

	/**
	 * @param keyContext the keyContext to set
	 */
	public void setKeyContext(KeyContext keyContext) {
		this.keyContext = keyContext;
	}

}