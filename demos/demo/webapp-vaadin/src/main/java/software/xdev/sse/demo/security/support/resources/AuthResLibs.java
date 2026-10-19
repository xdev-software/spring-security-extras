package software.xdev.sse.demo.security.support.resources;

import java.util.stream.Stream;


public final class AuthResLibs
{
	static final String PREFIX = "/lib/";
	public static final String BOOTSTRAP_JS = PREFIX + "bootstrap-5.3.3.bundle.min.js";
	public static final String BOOTSTRAP_JS_MAP = PREFIX + "bootstrap.bundle.min.js.map";
	public static final String BOOTSTRAP_CSS = PREFIX + "bootstrap-5.3.3.min.css";
	public static final String THEME_JS = PREFIX + "theme.js";
	public static final String THEME_CSS = PREFIX + "theme.css";
	
	public static Stream<String> all()
	{
		return Stream.of(
			BOOTSTRAP_JS,
			BOOTSTRAP_JS_MAP,
			BOOTSTRAP_CSS,
			THEME_JS,
			THEME_CSS);
	}
	
	private AuthResLibs()
	{
	}
}
