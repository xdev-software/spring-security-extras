package software.xdev.sse.codec.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class SHA256Hashing
{
	private static final String SHA_256 = "SHA-256";
	
	private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
	
	private SHA256Hashing()
	{
	}
	
	/**
	 * @apiNote Note that SHA256 gets slower the longer the input is. Ensure that the input length is limited.
	 */
	public static String hash(final String input)
	{
		if(input == null)
		{
			return null;
		}
		
		return hash(input.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * @apiNote Note that SHA256 gets slower the longer the input is. Ensure that the input length is limited.
	 */
	@SuppressWarnings("checkstyle:MagicNumber")
	public static String hash(final byte[] input)
	{
		if(input == null)
		{
			return null;
		}
		
		try
		{
			final MessageDigest digest = MessageDigest.getInstance(SHA_256);
			final byte[] bytes = digest.digest(input);
			
			final StringBuilder sb = new StringBuilder(2 * bytes.length);
			for(final byte b : bytes)
			{
				sb.append(HEX_DIGITS[b >> 4 & 0xf]).append(HEX_DIGITS[b & 0xf]);
			}
			return sb.toString();
		}
		// Will likely never happen
		catch(final NoSuchAlgorithmException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
