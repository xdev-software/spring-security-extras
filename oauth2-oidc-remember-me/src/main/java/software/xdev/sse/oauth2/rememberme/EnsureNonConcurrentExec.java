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
package software.xdev.sse.oauth2.rememberme;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Ensures that a function is not executed multiple times concurrently.
 */
public class EnsureNonConcurrentExec<K, V>
{
	private static final Logger LOG = LoggerFactory.getLogger(EnsureNonConcurrentExec.class);
	
	protected final Map<K, Lock> keyLocks = new ConcurrentHashMap<>();
	protected final Map<Lock, SavedResult<V>> lockResultsCache = Collections.synchronizedMap(new WeakHashMap<>());
	
	protected final Function<RuntimeException, SavedResult<V>> onException;
	
	public EnsureNonConcurrentExec(final Function<RuntimeException, SavedResult<V>> onException)
	{
		this.onException = onException;
	}
	
	/**
	 * @param key          The key to lock
	 * @param safeExecFunc The function to execute
	 * @return <ul>
	 * <li>computed result</li>
	 * <li>cached result</li>
	 * <li><code>null</code>on Exception when cached</li>
	 * </ul>
	 */
	public V execute(final K key, final Function<K, V> safeExecFunc)
	{
		// Create or acquire lock based on key if required
		final Lock lock = this.keyLocks.computeIfAbsent(key, x -> new ReentrantLock());
		// Ensure that only one execution can enter
		lock.lock();
		
		LOG.trace("Acquired lock for '{}'", key);
		
		// Check if was already computed
		final SavedResult<V> cached = this.lockResultsCache.get(lock);
		if(cached != null)
		{
			lock.unlock();
			LOG.trace("Returning result for '{}'", key);
			return cached.valueOrException();
		}
		
		try
		{
			final V value = safeExecFunc.apply(key);
			
			this.lockResultsCache.put(lock, new SavedResult<>(value));
			LOG.trace("Saving result for '{}'", key);
			
			return value;
		}
		catch(final RuntimeException rex)
		{
			this.lockResultsCache.put(lock, this.onException.apply(rex));
			LOG.trace("Saving empty result for '{}' due to exception", key);
			
			throw rex;
		}
		finally
		{
			// Unlock and remove
			lock.unlock();
			this.keyLocks.remove(key);
		}
	}
	
	public record SavedResult<V>(
		V value,
		RuntimeException rex
	)
	{
		public SavedResult(final V value)
		{
			this(value, null);
		}
		
		V valueOrException()
		{
			if(this.rex != null)
			{
				throw this.rex;
			}
			return this.value;
		}
	}
}
