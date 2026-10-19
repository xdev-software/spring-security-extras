package software.xdev.sse.demo.persistence.jpa.dto;

@SuppressWarnings("java:S6218") // Never used for equals/hashcode
public record AuthRememberMeSecretDTO(
	String identifier,
	String cryptoAlgorithm,
	byte[] secret,
	String userEmailAddress
)
{
}
