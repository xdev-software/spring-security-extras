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
package software.xdev.sse.vaadin.csp;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Set;

import software.xdev.sse.csp.CSPProvider;


public class VaadinDefaultCSPProvider implements CSPProvider
{
	@Override
	public Map<String, Set<String>> cspValues()
	{
		return Map.ofEntries(
			entry(
				DEFAULT_SRC,
				// https://vaadin.com/docs/v14/flow/advanced/framework-security/#content-security-policy-csp-set-to-unsafe-values
				// https://gist.github.com/Wnt/7d61cae6c9ac5ec11209a0932bac9a6f
				Set.of(SELF)),
			entry(SCRIPT_SRC, Set.of(SELF, DATA, UNSAFE_INLINE, UNSAFE_EVAL)),
			entry(STYLE_SRC, Set.of(SELF, UNSAFE_INLINE)),
			entry(FONT_SRC, Set.of(SELF, DATA)),
			// data - Required for login
			entry(IMG_SRC, Set.of(SELF, DATA)),
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/object-src
			// https://csp.withgoogle.com/docs/strict-csp.html
			entry(OBJECT_SRC, Set.of(NONE)),
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/base-uri
			entry(BASE_URI, Set.of(SELF)),
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/form-action
			// NOTE: When using 'self':
			// * Webkit based Browsers have problems here: https://github.com/w3c/webappsec-csp/issues/8
			//
			// As of 2024-03 CSP3 added 'unsafe-allow-redirects', however it's not implemented by any browser yet.
			// Fallback for now '*'
			// Required for logout
			entry(FORM_ACTION, Set.of(ALL)),
			entry(FRAME_SRC, Set.of(SELF)),
			// https://stackoverflow.com/a/40417609
			// Replaces X-Frame-Options
			entry(FRAME_ANCESTORS, Set.of(SELF)));
	}
}
