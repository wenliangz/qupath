/*-
 * #%L
 * This file is part of QuPath.
 * %%
 * Copyright (C) 2014 - 2016 The Queen's University of Belfast, Northern Ireland
 * Contact: IP Management (ipmanagement@qub.ac.uk)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package qupath.lib.projects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Read QuPath projects.
 * <p>
 * Projects should now be written with {@link Project#syncChanges()}
 * 
 * @author Pete Bankhead
 *
 */
public class ProjectIO {

	final private static Logger logger = LoggerFactory.getLogger(ProjectIO.class);
	
	public static final String DEFAULT_PROJECT_NAME = "project";
	
	public static final String DEFAULT_PROJECT_EXTENSION = "qpproj";
	
	/**
	 * Read project from URI.  Currently, this assumes that the URI refers to a local file.
	 * 
	 * @param uri
	 * @param cls
	 * @return
	 */
	public static <T> Project<T> loadProject(final URI uri, final Class<T> cls) {
		return loadProject(new File(uri), cls);
	}
	
	/**
	 * Load a project from a local file.
	 * 
	 * @param fileProject
	 * @param cls
	 * @return
	 */
	public static <T> Project<T> loadProject(final File fileProject, final Class<T> cls) {
		try (Reader fileReader = new BufferedReader(new FileReader(fileProject))){
			Gson gson = new Gson();
			JsonObject element = gson.fromJson(fileReader, JsonObject.class);
			// Didn't have the foresight to add a version number from the start...
			if (!element.has("version")) {
				return LegacyProject.readFromFile(fileProject, cls);
			}
			return (Project<T>)DefaultProject.loadFromFile(fileProject);
		} catch (Exception e) {
			logger.error("Error loading project", e);
			return null;
		}
	}
	

	/**
	 * Get the default extension for a QuPath project file.
	 * 
	 * @return
	 */
	public static String getProjectExtension(boolean includePeriod) {
		return includePeriod ? "." + DEFAULT_PROJECT_EXTENSION : DEFAULT_PROJECT_EXTENSION;
	}
	
	/**
	 * Get the default extension for a QuPath project file, without the 'dot'.
	 * @return
	 * 
	 * @see ProjectIO#getProjectExtension(boolean)
	 */
	public static String getProjectExtension() {
		return DEFAULT_PROJECT_EXTENSION;
	}




}
