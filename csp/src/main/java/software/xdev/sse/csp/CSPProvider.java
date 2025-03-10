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
