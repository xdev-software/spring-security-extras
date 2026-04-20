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
 * Provides support Content Security Policy.
 * <p/>
 * {@link software.xdev.sse.csp.CSPProvider CSPProviders} can be located in multiple independent modules and are loaded
 * dynamically by {@link software.xdev.sse.csp.CSPGenerator} to build the CSP.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP">
 * MDN Content-Security-Policy guide</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy">
 * MDN Content-Security-Policy headers</a>
 */
package software.xdev.sse.csp;
