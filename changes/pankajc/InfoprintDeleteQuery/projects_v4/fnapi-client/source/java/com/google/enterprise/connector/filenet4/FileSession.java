/*
 * Copyright 2009 Google Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 */
package com.google.enterprise.connector.filenet4;

import com.google.enterprise.connector.filenet4.filewrap.IConnection;
import com.google.enterprise.connector.filenet4.filewrap.IObjectFactory;
import com.google.enterprise.connector.filenet4.filewrap.IObjectStore;
import com.google.enterprise.connector.filenet4.filewrap.ISearch;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.TraversalManager;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSession implements Session {

	private IObjectFactory fileObjectFactory;
	private IObjectStore objectStore;
	private IConnection connection;
	private String displayUrl;
	private boolean isPublic;
	private String additionalWhereClause;
	private String additionalDeleteWhereClause;
	private HashSet includedMeta;
	private HashSet excludedMeta;
	private String dbTimezone;
	private static Logger LOGGER = Logger.getLogger(FileSession.class.getName());;

	public FileSession(String iObjectFactory, String userName,
			String userPassword, String objectStoreName, String displayUrl,
			String contentEngineUri, boolean isPublic,
			String additionalWhereClause, String additionalDeleteWhereClause,
			HashSet includedMeta, HashSet excludedMeta, String dbTimezone)
			throws RepositoryException, RepositoryLoginException {

		setFileObjectFactory(iObjectFactory);

		LOGGER.info("getting connection for content engine: "
				+ contentEngineUri);
		connection = fileObjectFactory.getConnection(contentEngineUri);

		LOGGER.info("trying to access object store: " + objectStoreName
				+ " for user: " + userName);
		objectStore = fileObjectFactory.getObjectStore(objectStoreName, connection, userName, userPassword);

		LOGGER.info("objectStore ok user:" + userName);

		this.displayUrl = getDisplayURL(displayUrl, objectStoreName);
		this.isPublic = isPublic;
		this.additionalWhereClause = additionalWhereClause;
		this.additionalDeleteWhereClause = additionalDeleteWhereClause;
		this.includedMeta = includedMeta;
		this.excludedMeta = excludedMeta;
		this.dbTimezone = dbTimezone;
	}

	private String getDisplayURL(String displayUrl, String objectStoreName) {
		if (displayUrl.endsWith("/getContent/")) {
			displayUrl = displayUrl.substring(0, displayUrl.length() - 1);
		}
		if (displayUrl.contains("/getContent")
				&& displayUrl.endsWith("/getContent")) {
			return displayUrl + "?objectStoreName=" + objectStoreName
					+ "&objectType=document&versionStatus=1&vsId=";
		} else {
			return displayUrl + "/getContent?objectStoreName="
					+ objectStoreName
					+ "&objectType=document&versionStatus=1&vsId=";
		}
	}

	private void setFileObjectFactory(String objectFactory)
			throws RepositoryException {

		try {
			fileObjectFactory = (IObjectFactory) Class.forName(objectFactory).newInstance();
		} catch (InstantiationException e) {
			LOGGER.log(Level.WARNING, "Unable to instantiate the class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory ");
			throw new RepositoryException(
					"Unable to instantiate the class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory ",
					e);
		} catch (IllegalAccessException e) {
			LOGGER.log(Level.WARNING, "Access denied to class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory ");
			throw new RepositoryException(
					"Access denied to class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory ",
					e);
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.WARNING, "The class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory not found");
			throw new RepositoryException(
					"The class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory not found",
					e);
		}

	}

	public TraversalManager getTraversalManager() throws RepositoryException {
		// logger.info("getTraversalManager");
		FileTraversalManager fileQTM = new FileTraversalManager(
				fileObjectFactory, objectStore, this.isPublic, this.displayUrl,
				this.additionalWhereClause, this.additionalDeleteWhereClause,
				this.includedMeta, this.excludedMeta, this.dbTimezone);
		return fileQTM;
	}

	public AuthenticationManager getAuthenticationManager()
			throws RepositoryException {
		FileAuthenticationManager fileAm = new FileAuthenticationManager(
				connection);
		return fileAm;
	}

	public AuthorizationManager getAuthorizationManager()
			throws RepositoryException {

		FileAuthorizationManager fileAzm = new FileAuthorizationManager(
				connection, objectStore);
		return fileAzm;
	}

	public ISearch getSearch() throws RepositoryException {
		ISearch search = fileObjectFactory.getSearch(objectStore);
		return search;
	}

}
