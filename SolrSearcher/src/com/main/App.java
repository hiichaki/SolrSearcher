package com.main;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * Sample app
 *
 */
public class App {
	public static void main(String[] args) throws SolrServerException, IOException {
		String url = "";
		String httpAuthUser = "";
		String httpAuthPass = "";
		// Configure latests Apache Http Components http client
		HttpClient httpClient = new DefaultHttpClient();
		if (httpAuthUser != null && httpAuthPass != null) {
			AbstractHttpClient client = (AbstractHttpClient) httpClient;
			App a = new App();
			client.addRequestInterceptor(a.new PreEmptiveBasicAuthenticator(httpAuthUser, httpAuthPass));
		}
		// Configure XMLResponseParser as standard javabin parser does not work
		// with 1.4
		SolrServer solr = new HttpSolrServer(url, httpClient, new XMLResponseParser());

		// Query for the data just added
		SolrQuery query = new SolrQuery();
		query.setQuery("description:/[0-9]/");
		query.setFields("id", "category_id", "description");
		query.setRows(2147483647);
		QueryResponse response = solr.query(query);
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> si = list.iterator();
		// System.out.println("Solr document" + list.getNumFound());

		String pattern = "(.*)[{%]*[^:]*[%}](.*)";

		// Create a Pattern object

		while (si.hasNext()) {
			String tmp;
			if ((tmp = si.next().toString()).matches(pattern)) {
				System.out.println(tmp);
			}

		}
	}

	protected class PreEmptiveBasicAuthenticator implements HttpRequestInterceptor {
		private final UsernamePasswordCredentials credentials;

		public PreEmptiveBasicAuthenticator(String user, String pass) {
			credentials = new UsernamePasswordCredentials(user, pass);
		}

		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			request.addHeader(BasicScheme.authenticate(credentials, "US-ASCII", false));
		}
	}
}