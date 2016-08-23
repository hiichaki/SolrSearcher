package com.main;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class Searcher {

	private String url;
	private String httpAuthUser;
	private String httpAuthPass;
	private SolrServer solr;

	protected class PreEmptiveBasicAuthenticator implements HttpRequestInterceptor {
		private final UsernamePasswordCredentials credentials;

		public PreEmptiveBasicAuthenticator(String user, String pass) {
			credentials = new UsernamePasswordCredentials(user, pass);
		}

		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			request.addHeader(BasicScheme.authenticate(credentials, "US-ASCII", false));
		}
	}

	private void connect() {
		HttpClient httpClient = new DefaultHttpClient();
		if (httpAuthUser != null && httpAuthPass != null) {
			AbstractHttpClient client = (AbstractHttpClient) httpClient;
			Searcher a = new Searcher();
			client.addRequestInterceptor(a.new PreEmptiveBasicAuthenticator(httpAuthUser, httpAuthPass));
			solr = new HttpSolrServer(url, httpClient);
		}
	}

	public Searcher() {
		this.url = "";
		this.httpAuthUser = "";
		this.httpAuthPass = "";
		connect();

	}

	public Searcher(String url, String httpAuthUser, String httpAuthPass) {
		this.url = url;
		this.httpAuthUser = httpAuthUser;
		this.httpAuthPass = httpAuthPass;

		connect();

	}

	public SolrServer getSolr() {
		return solr;
	}

	public void setSolr(SolrServer solr) {
		this.solr = solr;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHttpAuthUser() {
		return httpAuthUser;
	}

	public void setHttpAuthUser(String httpAuthUser) {
		this.httpAuthUser = httpAuthUser;
	}

	public String getHttpAuthPass() {
		return httpAuthPass;
	}

	public void setHttpAuthPass(String httpAuthPass) {
		this.httpAuthPass = httpAuthPass;
	}

}
