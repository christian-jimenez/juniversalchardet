/**
 *
 */
package org.mozilla.universalchardet.common;

import java.io.File;

/**
 * @author Christian.Jimenez
 * @version 1.0
 */
public abstract class ResourceLoaderTest {

	public File getFileResource(final String resourceName) {
		ClassLoader classLoader = this.getClass().getClassLoader();

		return new File(classLoader.getResource(resourceName).getFile());
	}

}
