/*
 * jETeL/CloverETL - Java based ETL application framework.
 * Copyright (c) Javlin, a.s. (info@cloveretl.com)
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jetel.util.protocols.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultProxyAuthenticationHandler;
import org.apache.http.protocol.HttpContext;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.impl.SardineException;
import com.googlecode.sardine.impl.SardineImpl;
import com.googlecode.sardine.impl.handler.MultiStatusResponseHandler;
import com.googlecode.sardine.impl.methods.HttpPropFind;
import com.googlecode.sardine.model.Allprop;
import com.googlecode.sardine.model.Multistatus;
import com.googlecode.sardine.model.Propfind;
import com.googlecode.sardine.model.Response;
import com.googlecode.sardine.util.SardineUtil;

class WebdavClientImpl extends SardineImpl {
	
	private static final String UTF_8 = "UTF-8";

	private AbstractHttpClient client;
	
	WebdavClientImpl(String username, String password, ProxyConfiguration proxyConfiguration) {
		super(username, password, proxyConfiguration.getProxySelector());
		
		// prefer basic authentication, as NTLM does not seem to work
		client.setProxyAuthenticationHandler(new DefaultProxyAuthenticationHandler() {

			@Override
			protected List<String> getAuthPreferences(HttpResponse response, HttpContext context) {
	        	return Arrays.asList(
	        			AuthPolicy.BASIC,
	        			AuthPolicy.DIGEST,
	        			AuthPolicy.SPNEGO,
	        			AuthPolicy.NTLM);
			}
            
        });
		
		Proxy proxy = proxyConfiguration.getProxy();
		if (proxy != null && proxy != Proxy.NO_PROXY) {
			URI proxyUri = URI.create(proxyConfiguration.getProxyString());
			String userInfo = proxyUri.getUserInfo();
			if (userInfo != null) {
				HttpHost proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort());
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
				setProxyCredentials(client, proxyHost.getHostName(), proxyHost.getPort(), userInfo);
			}
		}
	}
	
	/**
	 * Combines <code>user</code> and <code>password</code> into a userInfo string "user:password".
	 * Handles <code>null</code> values.
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public static String getUserInfo(String user, String password) {
		if ((user != null) && (password != null)) {
			return user + ":" + password;
		} else if (user != null) {
			return user;
		}
		
		return null;
	}
	
//	/**
//	 * This uses the nonstandard "http.proxyUser" and "http.proxyPassword"
//	 * System properties.
//	 * 
//	 * @param protocol
//	 */
//	private void setDefaultProxyCredentials(String protocol) {
//		protocol = protocol.toLowerCase();
//		String proxyUserInfo = getUserInfo(System.getProperty(protocol + ".proxyUser"), System.getProperty(protocol + ".proxyPassword"));
//		String proxyHost = System.getProperty(protocol + ".proxyHost"); // null means any host
//		String proxyPortString = System.getProperty(protocol + ".proxyPort");
//		int proxyPort = -1; // -1 means any port
//		if (proxyPortString != null) {
//			proxyPort = Integer.parseInt(proxyPortString);
//		}
//		if (proxyUserInfo != null) {
//			setProxyCredentials(client, proxyHost, proxyPort, proxyUserInfo);
//		}
//	}

	/*
	 * Stores the HTTP client instance so that client.setProxyAuthenticationHandler()
	 * can be called later.
	 */
	@Override
	protected AbstractHttpClient createDefaultClient(ProxySelector selector) {
		this.client = super.createDefaultClient(selector);
		return client;
	}
	
	private void setProxyCredentials(AbstractHttpClient client, String hostName, int port, String userInfo) {
		if (userInfo == null) {
			return;
		}
		String username;
		String password;
        int atColon = userInfo.indexOf(':');
        if (atColon >= 0) {
            username = userInfo.substring(0, atColon);
            password = userInfo.substring(atColon + 1);
        } else {
            username = userInfo;
            password = null;
        }
		Credentials credentials = new UsernamePasswordCredentials(username, password);
		InetSocketAddress addr = new InetSocketAddress(hostName, port);
		String hostAddress = addr.getAddress().getHostAddress(); // convert hostname to IP
		client.getCredentialsProvider().setCredentials(new AuthScope(hostName, port, AuthScope.ANY_REALM, AuthPolicy.BASIC), credentials);
		client.getCredentialsProvider().setCredentials(new AuthScope(hostAddress, port, AuthScope.ANY_REALM, AuthPolicy.BASIC), credentials);
		client.getCredentialsProvider().setCredentials(new AuthScope(hostName, port, AuthScope.ANY_REALM, AuthPolicy.DIGEST), credentials);
		client.getCredentialsProvider().setCredentials(new AuthScope(hostAddress, port, AuthScope.ANY_REALM, AuthPolicy.DIGEST), credentials);
		
		// fallback
		credentials = new NTCredentials(username, password, "", "");
		client.getCredentialsProvider().setCredentials(new AuthScope(hostName, port, AuthScope.ANY_REALM, AuthPolicy.NTLM), credentials);
		client.getCredentialsProvider().setCredentials(new AuthScope(hostAddress, port, AuthScope.ANY_REALM, AuthPolicy.NTLM), credentials);
	}
	
	/**
	 * Execute PROPFIND with depth 0.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	// not used now, but may be useful for file operations
	public DavResource info(String url) throws IOException {
		HttpPropFind entity = new HttpPropFind(url);
		entity.setDepth("0");
		Propfind body = new Propfind();
		body.setAllprop(new Allprop());
		entity.setEntity(new StringEntity(SardineUtil.toXml(body), UTF_8));
		Multistatus multistatus = execute(entity, new MultiStatusResponseHandler());
		List<Response> responses = multistatus.getResponse();
		List<DavResource> resources = new ArrayList<DavResource>(responses.size());
		for (Response resp : responses) {
			try {
				resources.add(new DavResource(resp));
			} catch (URISyntaxException e) {
				// Ignore resource with invalid URI
			}
		}
		return !resources.isEmpty() ? resources.get(0) : null;
	}

	/**
	 * Check whether remote directory exists with the help of GET method instead of HEAD used by Sardine.exists.
	 * See http://code.google.com/p/sardine/issues/detail?id=48 for more information and motivation.
	 * 
	 * This is quite inefficient, how about PROPFIND?
	 * See {@link #info(String)}.
	 * 
	 * @param url
	 *            Path to the directory.
	 * @return True if the directory exists.
	 * @throws IOException
	 */
	public boolean dirExists(String url) throws IOException {
		try {
			InputStream is = get(url);
			if (is == null) {
				throw new IOException("GET " + url + " failed");
			}
			is.close();
			return true;
		} catch (SardineException ex) {
			if (ex.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				return false;
			}
			throw ex;
		}
	}
}