/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.io;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Abstract implementation of <code>IOFactory</code> which provides basic functionality.
 *
 * @param <T> The type of objects which should be produced by this factory
 */
public abstract class AbstractIOFactory<T> implements IOFactory<T> {
	protected final Map<String, Class<? extends T>> entries = new HashMap<String, Class<? extends T>>();

	protected AbstractIOFactory(String propFileName) {
		// Retrieve property-files
		Enumeration<URL> propFiles = null;
		try {
			propFiles = getClass().getClassLoader().getResources(propFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (propFiles != null) {
			Properties props = new Properties();
			while (propFiles.hasMoreElements()) {
				URL propURL = propFiles.nextElement();
				InputStream stream = null;
				try {
					stream = propURL.openStream();
					props.load(stream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// Parse property files and register entries as items
				for (Map.Entry<Object, Object> prop : props.entrySet()) {
					String mimeType = (String) prop.getKey();
					String className = (String) prop.getValue();
					Class<?> clazz = null;
					try {
						clazz = Class.forName(className);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}
					// FIXME: Missing type safety check
					entries.put(mimeType, (Class<? extends T>) clazz);
				}
			}
		}
	}

	@Override
	public IOCapabilities getCapabilities(String mimeType) {
		Class<? extends T> clazz = entries.get(mimeType);
		try {
			Method capabilitiesGetter = clazz.getMethod("getCapabilities");
			Set<IOCapabilities> capabilities = (Set<IOCapabilities>) capabilitiesGetter.invoke(clazz);
			for (IOCapabilities c : capabilities) {
				if (c.getMimeType().equals(mimeType)) {
					return c;
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public IOCapabilities[] getCapabilities() {
		IOCapabilities[] caps = new IOCapabilities[entries.size()];
		int i=0;
		for (String mimeType : entries.keySet()) {
			caps[i++] = getCapabilities(mimeType);
		}
		return caps;
	}

	@Override
	public String[] getSupportedFormats() {
		String[] formats = new String[entries.size()];
		entries.keySet().toArray(formats);
		return formats;
	}

	@Override
	public boolean isFormatSupported(String mimeType) {
		return entries.containsKey(mimeType);
	}
}