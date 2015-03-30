/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.location;

import java.net.MalformedURLException;
import java.net.URL;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;

/**
 * A special location resolver that uses Strings like URLs, but with more options
 *
 */

public class ComponentLocationResolverForTenant implements LocationResolver {

	public static final String module = ComponentLocationResolverForTenant.class.getName();

	public URL resolveLocation(String location) throws MalformedURLException {
	StringBuffer baseLocation = ComponentLocationResolverForTenant.getBaseLocation(location);
	StringBuffer baseTenantLocation = ComponentLocationResolverForTenant.getTenantBaseLocation(location);
	URL fileUrl = UtilURL.fromFilename(baseTenantLocation.toString());
	if(fileUrl == null)
		fileUrl = UtilURL.fromFilename(baseLocation.toString());

	if (fileUrl == null) {
		String fileToLoad = FlexibleLocation.stripLocationType(location);
		fileUrl = FlexibleLocation.findResourceInClasspath(fileToLoad);
		if (fileUrl == null)
			Debug.logWarning("Unable to get file URL for component location; expanded location was [" + baseLocation
					+ "], original location was [" + location + "]", module);
	}

	return fileUrl;
	}

	public static StringBuffer getBaseLocation(String location) throws MalformedURLException {
	StringBuffer baseLocation = new StringBuffer(FlexibleLocation.stripLocationType(location));

	// componentName is between the first slash and the second
	int firstSlash = baseLocation.indexOf("/");
	int secondSlash = baseLocation.indexOf("/", firstSlash + 1);
	if (firstSlash != 0 || secondSlash == -1) {
		throw new MalformedURLException("Bad component location [" + location + "]: base location missing slashes ["
				+ baseLocation + "], first=" + firstSlash + ", second=" + secondSlash
				+ "; should be like: component://{component-name}/relative/path");
	}
	String componentName = baseLocation.substring(firstSlash + 1, secondSlash);

	// got the componentName, now remove it from the baseLocation, removing the second slash too (just in case the
	// rootLocation has one)
	baseLocation.delete(0, secondSlash + 1);

	String rootLocation;
	try {
		rootLocation = ComponentConfig.getRootLocation(componentName);
	} catch (ComponentException e) {
		String errMsg = "Could not get root location for component with name [" + componentName + "], error was: "
				+ e.toString();
		Debug.logError(e, errMsg, module);
		throw new MalformedURLException(errMsg);
	}

	// if there is not a forward slash between the two, add it
	if (baseLocation.charAt(0) != '/' && rootLocation.charAt(rootLocation.length() - 1) != '/') {
		baseLocation.insert(0, '/');
	}

	// insert the root location and we're done
	baseLocation.insert(0, rootLocation);

	return baseLocation;
	}

	public static StringBuffer getTenantBaseLocation(String location) throws MalformedURLException {
	StringBuffer baseLocation = new StringBuffer(FlexibleLocation.stripLocationType(location));

	// componentName is between the first slash and the second
	int firstSlash = baseLocation.indexOf("/");
	int secondSlash = baseLocation.indexOf("/", firstSlash + 1);
	if (firstSlash != 0 || secondSlash == -1) {
		throw new MalformedURLException("Bad component location [" + location + "]: base location missing slashes ["
				+ baseLocation + "], first=" + firstSlash + ", second=" + secondSlash
				+ "; should be like: component://{component-name}/relative/path");
	}
	String componentName = baseLocation.substring(firstSlash + 1, secondSlash);

	// got the componentName, now remove it from the baseLocation, removing the second slash too (just in case the
	// rootLocation has one)
	baseLocation.delete(0, secondSlash + 1);

	String rootLocation;
	try {
		rootLocation = ComponentConfig.getRootLocation(componentName);
	} catch (ComponentException e) {
		String errMsg = "Could not get root location for component with name [" + componentName + "], error was: "
				+ e.toString();
		Debug.logError(e, errMsg, module);
		throw new MalformedURLException(errMsg);
	}

	// if there is not a forward slash between the two, add it
	if (baseLocation.charAt(0) != '/' && rootLocation.charAt(rootLocation.length() - 1) != '/') {
		baseLocation.insert(0, '/');
	}
	
	int lastSlash = baseLocation.lastIndexOf("/");
	
	baseLocation.insert(lastSlash + 1,  LoginTenantInformation.getTenantId() + "/");

	// insert the root location and we're done
	baseLocation.insert(0, rootLocation);

	return baseLocation;
	}
}
