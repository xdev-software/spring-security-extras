package software.xdev.sse.web.loginurl;

public class DefaultLoginUrlStore implements LoginUrlStore
{
	public static final String DEFAULT_LOGIN = "/login";
	
	protected String loginUrl = DEFAULT_LOGIN;
	
	@Override
	public void setLoginUrl(final String loginUrl)
	{
		this.loginUrl = loginUrl;
	}
	
	@Override
	public String getLoginUrl()
	{
		return this.loginUrl;
	}
}
