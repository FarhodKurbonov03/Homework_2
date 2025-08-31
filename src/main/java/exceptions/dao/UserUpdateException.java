package exceptions.dao;

public class UserUpdateException extends RuntimeException {
	public UserUpdateException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
