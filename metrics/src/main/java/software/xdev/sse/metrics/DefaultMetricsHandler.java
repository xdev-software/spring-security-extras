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
package software.xdev.sse.metrics;

public abstract class DefaultMetricsHandler implements MetricsHandler
{
	protected static final String PREFIX = SSESharedMetrics.PREFIX;
	protected static final String TAG_OUTCOME = SSESharedMetrics.TAG_OUTCOME;
	protected static final String TAG_OPERATION = SSESharedMetrics.TAG_OPERATION;
	
	private boolean enabled;
	
	protected DefaultMetricsHandler(final boolean enabled)
	{
		this.setEnabled(enabled);
	}
	
	protected void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@Override
	public boolean enabled()
	{
		return this.enabled;
	}
}
