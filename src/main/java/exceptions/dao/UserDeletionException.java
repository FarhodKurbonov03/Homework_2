package exceptions.dao;

public class UserDeletionException extends RuntimeException {
	public UserDeletionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
