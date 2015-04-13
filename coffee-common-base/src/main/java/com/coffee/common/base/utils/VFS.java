package com.coffee.common.base.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class VFS {

	private static final byte[] JAR_MAGIC = { 'P', 'K', 3, 4 };

	/** Get a class by name. If the class is not found then return null. */
	public static Class<?> getClass(String className) {
		try {
			return Thread.currentThread().getContextClassLoader()
					.loadClass(className);
			// return ReflectUtil.findClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Get a list of {@link URL}s from the context classloader for all the
	 * resources found at the specified path.
	 *
	 * @param path
	 *            The resource path.
	 * @return A list of {@link URL}s, as returned by {@link ClassLoader#getResources(String)}.
	 * @throws IOException
	 *             If I/O errors occur
	 */
	public static List<URL> getResources(String path) throws IOException {
		return Collections.list(Thread.currentThread().getContextClassLoader()
				.getResources(path));
	}

	/**
	 * Recursively list the full resource path of all the resources that are
	 * children of the resource identified by a URL.
	 *
	 * @param url
	 *            The URL that identifies the resource to list.
	 * @param path
	 *            The path to the resource that is identified by the URL.
	 *            Generally, this is the value passed to {@link #getResources(String)} to get the resource URL.
	 * @return A list containing the names of the child resources.
	 * @throws IOException
	 *             If I/O errors occur
	 */
	public List<String> list(URL url, String path) throws IOException {
		InputStream is = null;
		try {
			List<String> resources = new ArrayList<String>();

			// First, try to find the URL of a JAR file containing the requested
			// resource. If a JAR
			// file is found, then we'll list child resources by reading the
			// JAR.
			URL jarUrl = findJarForResource(url);
			if (jarUrl != null) {
				is = jarUrl.openStream();
				resources = listResources(new JarInputStream(is), path);
			} else {
				List<String> children = new ArrayList<String>();
				try {
					if (isJar(url)) {
						// Some versions of JBoss VFS might give a JAR stream
						// even if the resource
						// referenced by the URL isn't actually a JAR
						is = url.openStream();
						JarInputStream jarInput = new JarInputStream(is);
						for (JarEntry entry; (entry = jarInput
								.getNextJarEntry()) != null;) {
							children.add(entry.getName());
						}
					} else {
						/*
						 * Some servlet containers allow reading from directory
						 * resources like a text file, listing the child
						 * resources one per line. However, there is no way to
						 * differentiate between directory and file resources
						 * just by reading them. To work around that, as each
						 * line is read, try to look it up via the class loader
						 * as a child of the current resource. If any line fails
						 * then we assume the current resource is not a
						 * directory.
						 */
						is = url.openStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(is));
						List<String> lines = new ArrayList<String>();
						for (String line; (line = reader.readLine()) != null;) {
							lines.add(line);
							if (getResources(path + "/" + line).isEmpty()) {
								lines.clear();
								break;
							}
						}

						if (!lines.isEmpty()) {
							children.addAll(lines);
						}
					}
				} catch (FileNotFoundException e) {
					/*
					 * For file URLs the openStream() call might fail, depending
					 * on the servlet container, because directories can't be
					 * opened for reading. If that happens, then list the
					 * directory directly instead.
					 */
					if ("file".equals(url.getProtocol())) {
						File file = new File(url.getFile());
						if (file.isDirectory()) {
							children = Arrays.asList(file.list());
						}
					} else {
						// No idea where the exception came from so rethrow it
						throw e;
					}
				}

				// The URL prefix to use when recursively listing child
				// resources
				String prefix = url.toExternalForm();
				if (!prefix.endsWith("/")) {
					prefix = prefix + "/";
				}

				// Iterate over immediate children, adding files and recursing
				// into directories
				for (String child : children) {
					String resourcePath = path + "/" + child;
					resources.add(resourcePath);
					URL childUrl = new URL(prefix + child);
					resources.addAll(list(childUrl, resourcePath));
				}
			}

			return resources;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
			}
		}
	}

	protected List<String> listResources(JarInputStream jar, String path)
			throws IOException {
		// Include the leading and trailing slash when matching names
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}

		// Iterate over the entries and collect those that begin with the
		// requested path
		List<String> resources = new ArrayList<String>();
		for (JarEntry entry; (entry = jar.getNextJarEntry()) != null;) {
			if (!entry.isDirectory()) {
				// Add leading slash if it's missing
				String name = entry.getName();
				if (!name.startsWith("/")) {
					name = "/" + name;
				}

				// Check file name
				if (name.startsWith(path)) {
					resources.add(name.substring(1)); // Trim leading slash
				}
			}
		}
		return resources;
	}

	protected URL findJarForResource(URL url) throws MalformedURLException {
		// If the file part of the URL is itself a URL, then that URL probably
		// points to the JAR
		try {
			for (;;) {
				url = new URL(url.getFile());
			}
		} catch (MalformedURLException e) {
			// This will happen at some point and serves as a break in the loop
		}

		// Look for the .jar extension and chop off everything after that
		StringBuilder jarUrl = new StringBuilder(url.toExternalForm());
		int index = jarUrl.lastIndexOf(".jar");
		if (index >= 0) {
			jarUrl.setLength(index + 4);
		} else {
			return null;
		}

		// Try to open and test it
		try {
			URL testUrl = new URL(jarUrl.toString());
			if (isJar(testUrl)) {
				return testUrl;
			} else {
				// WebLogic fix: check if the URL's file exists in the
				// filesystem.
				jarUrl.replace(0, jarUrl.length(), testUrl.getFile());
				File file = new File(jarUrl.toString());

				// File name might be URL-encoded
				if (!file.exists()) {
					try {
						file = new File(URLEncoder.encode(jarUrl.toString(),
								"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(
								"Unsupported encoding?  UTF-8?  That's unpossible.");
					}
				}

				if (file.exists()) {
					testUrl = file.toURI().toURL();
					if (isJar(testUrl)) {
						return testUrl;
					}
				}
			}
		} catch (MalformedURLException e) {
		}

		return null;
	}

	protected boolean isJar(URL url) {
		return isJar(url, new byte[JAR_MAGIC.length]);
	}

	protected boolean isJar(URL url, byte[] buffer) {
		InputStream is = null;
		try {
			is = url.openStream();
			is.read(buffer, 0, JAR_MAGIC.length);
			if (Arrays.equals(buffer, JAR_MAGIC)) {
				return true;
			}
		} catch (Exception e) {
			// Failure to read the stream means this is not a JAR
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}

		return false;
	}

	/**
	 * Recursively list the full resource path of all the resources that are
	 * children of all the resources found at the specified path.
	 *
	 * @param path
	 *            The path of the resource(s) to list.
	 * @return A list containing the names of the child resources.
	 * @throws IOException
	 *             If I/O errors occur
	 */
	public List<String> list(String path) throws IOException {
		List<String> names = new ArrayList<String>();
		for (URL url : getResources(path)) {
			names.addAll(list(url, path));
		}
		return names;
	}
}
