package cz.hatua.jtimelog.cats;

public class EmptyCatException extends IllegalArgumentException {
	static final long serialVersionUID = 1L;
	public EmptyCatException(String message) {
		super(message);
	}
	public EmptyCatException(String message, Throwable cause) {
		super(message, cause);
	}
}
