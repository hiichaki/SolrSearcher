package com.main;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

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
		SolrServer solr = new HttpSolrServer(url, httpClient);

		SolrQuery query = new SolrQuery();
		query.setQuery("description:/[0-9]/");
		query.setFields("id", "category_id", "description");
		query.setRows(2147483647);
		QueryResponse response = solr.query(query);
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> si = list.iterator();
		// System.out.println("Solr document" + list.getNumFound());

		// String pattern = "^{%[a-zA-Z]*\\ {0,}\\d{1,3}\\ {0,}%}$";
		// Pattern p = Pattern.compile(pattern);
		// Create a Pattern object
		PrintWriter writer = new PrintWriter("result.txt", "UTF-8");
		while (si.hasNext()) {
			String tmp = si.next().toString();
			int beginIndex = tmp.indexOf("{%");
			int endIndex = tmp.indexOf("%}", beginIndex);
			if (beginIndex != endIndex) {
				while (beginIndex > 0) {
					if (!tmp.substring(beginIndex, endIndex).contains(":")) {

						int idBegin = tmp.indexOf("id=");
						int idEnd = tmp.indexOf(",", idBegin);

						int categoryBegin = tmp.indexOf("category_id=");

						// int categoryEnd = tmp.indexOf("}", categoryBegin -
						// 1);
						// if (categoryEnd > categoryBegin + 10) {
						// categoryEnd = tmp.indexOf(",", categoryBegin - 1);
						// }

						String id = tmp.substring(idBegin, idEnd);
						String category = tmp.substring(categoryBegin, categoryBegin + 13);
						String verse = tmp.substring(beginIndex, endIndex + 2);

						writer.println(id + "\t" + category + "\t" + verse);

						System.out.println(tmp.substring(idBegin, idEnd));

						System.out.println(tmp.substring(categoryBegin, categoryBegin + 14));

						System.out.println(tmp.substring(beginIndex, endIndex + 2));
					}
					beginIndex = tmp.indexOf("{%", endIndex);
					endIndex = tmp.indexOf("%}", beginIndex);
				}
			}

		}
		writer.close();

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