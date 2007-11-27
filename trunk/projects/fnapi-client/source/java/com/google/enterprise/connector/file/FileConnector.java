package com.google.enterprise.connector.file;

import java.util.HashSet;

import com.google.enterprise.connector.file.FileSession;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class FileConnector implements Connector {

	private String object_factory;

	private String login;

	private String password;

	private String object_store;

	private String path_to_WcmApiConfig;

	private String workplace_display_url;

	private String is_public = "false";

	private String authentication_type;

	private String additional_where_clause;

	private HashSet included_meta;

	private HashSet excluded_meta;

	public Session login() throws RepositoryLoginException, RepositoryException {
		Session sess = null;
		if (!(object_factory == null || login == null || password == null
				|| object_store == null || workplace_display_url == null)) {

			sess = new FileSession(object_factory, login, password,
					object_store, path_to_WcmApiConfig, workplace_display_url,
					is_public.equals("on"), additional_where_clause,
					included_meta, excluded_meta);
		}
		return sess;

	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getObject_factory() {
		return object_factory;
	}

	public void setObject_factory(String objectFactory) {
		this.object_factory = objectFactory;
	}

	public String getObject_store() {
		return object_store;
	}

	public void setObject_store(String objectStoreName) {
		this.object_store = objectStoreName;
	}

	public String getPath_to_WcmApiConfig() {
		return path_to_WcmApiConfig;
	}

	public void setPath_to_WcmApiConfig(String pathToWcmApiConfig) {
		this.path_to_WcmApiConfig = pathToWcmApiConfig;
	}

	public String getWorkplace_display_url() {
		return workplace_display_url;
	}

	public void setWorkplace_display_url(String displayUrl) {
		this.workplace_display_url = displayUrl;
	}

	public String getIs_public() {
		return is_public;
	}

	public void setIs_public(String isPublic) {
		this.is_public = isPublic;
	}

	public String getAdditional_where_clause() {
		return additional_where_clause;
	}

	public void setAdditional_where_clause(String additionalWhereClause) {
		this.additional_where_clause = additionalWhereClause;
	}

	public String getAuthentication_type() {
		return authentication_type;
	}

	public void setAuthentication_type(String authenticationType) {
		this.authentication_type = authenticationType;
	}

	public HashSet getExcluded_meta() {
		return excluded_meta;
	}

	public void setExcluded_meta(HashSet excluded_meta) {
		this.excluded_meta = excluded_meta;
	}

	public HashSet getIncluded_meta() {
		return included_meta;
	}

	public void setIncluded_meta(HashSet included_meta) {
		this.included_meta = included_meta;
	}

}
