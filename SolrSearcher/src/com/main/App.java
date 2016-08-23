package com.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class App {

	
	
	/*
	 * {%1 Alpha 2%}
	 */
	public static void getPattern1(SolrServer solr)
			throws SolrServerException, FileNotFoundException, UnsupportedEncodingException {
		SolrQuery query = new SolrQuery();
		query.setQuery("description:/[0-9]/");
		query.setFields("id", "category_id", "description");
		query.setRows(2147483647);
		QueryResponse response = solr.query(query);
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> si = list.iterator();

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

	/*
	 * <em>*</em>
	 */
	public static void getPattern2(SolrServer solr)
			throws SolrServerException, FileNotFoundException, UnsupportedEncodingException {
		SolrQuery query = new SolrQuery();
		query.setQuery("htmldescription:*");
		query.setFields("id", "title", "category_id", "htmldescription");
		query.setRows(2147483647);
		QueryResponse response = solr.query(query);
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> si = list.iterator();
		PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
		while (si.hasNext()) {
			String tmp = si.next().toString();
			int beginIndex = tmp.indexOf("<em>");
			int endIndex = tmp.indexOf("</em>", beginIndex);

			if (beginIndex != endIndex) {
				int idBegin = tmp.indexOf("id=");
				int idEnd = tmp.indexOf(",", idBegin);

				int categoryBegin = tmp.indexOf("category_id=");

				int titleBegin = tmp.indexOf("title=");
				int titleEnd = tmp.indexOf("}", titleBegin);

				String id = tmp.substring(idBegin, idEnd);
				String category = replaceCategory(tmp.substring(categoryBegin + 12, categoryBegin + 13));
				String verse = tmp.substring(beginIndex, endIndex + 5);
				String title = tmp.substring(titleBegin, titleEnd);
				writer.write(String.format("%-9s %-10s %-82s %-20s%n ", id, category, title, verse));

			}
			beginIndex = tmp.indexOf("<em>", endIndex);
			endIndex = tmp.indexOf("</em>", beginIndex);
		}

		writer.close();

	}

	public static SolrDocumentList getSolrList(SolrServer solr, SolrQuery query) throws SolrServerException {
		QueryResponse response = solr.query(query);
		SolrDocumentList list = response.getResults();
		return list;
	}

	public static void getAnswer() {

	}

	public static String replaceCategory(String category) {
		switch (category) {
		case "1":
			return "topic";
		case "2":
			return "media";
		case "3":
			return "location";
		case "4":
			return "time";
		default:
			return null;
		}
	}

	public static void main(String[] args) throws SolrServerException, IOException {


	}


}