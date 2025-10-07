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
/**
 * Controls how
 * {@link org.springframework.security.config.annotation.web.builders.HttpSecurity
 * #securityMatcher(org.springframework.security.web.util.matcher.RequestMatcher)} is applied for sidecars.
 * <p>
 * By default, it ALWAYS uses {@link org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher}
 * instead of the internally used <code>MvcRequestMatcher (deprecated)</code> (or <code>AntPathRequestMatcher</code>
 * if MVC is not present) when calling <code>HTTPSecurity#securityMatcher(String...)</code>. This prevents unexpected
 * bugs that can occur when a url mapping (e.g. <code>/2025/*</code>) is registered for a servlet, which can result
 * in unwanted paths being picked up (e.g. <code>/2025/actuator</code>).
 * </p>
 * <p>
 * <i>This package is only designed to be used in Sidecars and not in the main application!</i>
 * </p>
 * @see <a href="https://github.com/xdev-software/spring-security-extras/issues/221">#221</a>
 */
package software.xdev.sse.web.sidecar.httpsecurity;
