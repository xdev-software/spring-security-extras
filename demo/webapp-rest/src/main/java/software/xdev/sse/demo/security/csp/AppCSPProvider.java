package software.xdev.sse.demo.security.csp;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import software.xdev.sse.csp.CSPProvider;


@Component
public class AppCSPProvider implements CSPProvider
{
	@Override
	public Map<String, Set<String>> cspValues()
	{
		return Map.ofEntries(
			entry(DEFAULT_SRC, Set.of(SELF)),
			entry(SCRIPT_SRC, Set.of(SELF, UNSAFE_INLINE)),
			entry(STYLE_SRC, Set.of(SELF, UNSAFE_INLINE)),
			entry(FONT_SRC, Set.of(SELF)),
			entry(IMG_SRC, Set.of(SELF, DATA)),
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/object-src
			// https://csp.withgoogle.com/docs/strict-csp.html
			entry(OBJECT_SRC, Set.of(NONE)),
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/base-uri
			entry(BASE_URI, Set.of(SELF)),
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/form-action
			// When using 'self':
			// * Webkit based Browsers have problems here: https://github.com/w3c/webapp-restsec-csp/issues/8
			// * Firefox is
			// As of 2024-03 CSP3 added 'unsafe-allow-redirects' however it's not implemented by any browser yet
			// Fallback for now '*'
			entry(FORM_ACTION, Set.of(ALL)),
			// Replaces X-Frame-Options
			entry(FRAME_SRC, Set.of(SELF)),
			entry(FRAME_ANCESTORS, Set.of(SELF)));
	}
}
