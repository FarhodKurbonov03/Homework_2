package exceptions.dao;

public class UserReadException extends RuntimeException {
	public UserReadException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
