/*
 * Copyright © 2025 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.sse.csp;

import java.util.Map;
import java.util.Set;


/**
 * Content-Security-Policy provider
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP">
 * MDN Content-Security-Policy guide</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy">
 * MDN Content-Security-Policy headers</a>
 */
public interface CSPProvider
{
	// Common Keys
	String DEFAULT_SRC = "default-src";
	String SCRIPT_SRC = "script-src";
	String STYLE_SRC = "style-src";
	String IMG_SRC = "img-src";
	String FONT_SRC = "font-src";
	String CONNECT_SRC = "connect-src";
	String MEDIA_SRC = "media-src";
	String OBJECT_SRC = "object-src";
	String BASE_URI = "base-uri";
	String FORM_ACTION = "form-action";
	String FRAME_SRC = "frame-src";
	String FRAME_ANCESTORS = "frame-ancestors";
	
	// Common Values
	String ALL = "*";
	String SELF = "'self'";
	String NONE = "'none'";
	String DATA = "data:";
	String UNSAFE_INLINE = "'unsafe-inline'";
	String UNSAFE_EVAL = "'unsafe-eval'";
	String UNSAFE_HASHES = "'unsafe-hashes'";
	
	Map<String, Set<String>> cspValues();
	
	default boolean hasOverwriteValues()
	{
		return false;
	}
	
	default Set<String> overwriteValues(final String key, final Set<String> allValues)
	{
		return allValues;
	}
}
