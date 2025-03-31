package software.xdev.sse.demo.entities.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = AuthRememberMeSecret.TABLE_NAME)
public class AuthRememberMeSecret extends Secret
{
	public static final String TABLE_NAME = "auth_remember_me_secret";
}
