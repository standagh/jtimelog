package cz.hatua.jtimelog.cats;

public class CommentCatException extends IllegalArgumentException {
	static final long serialVersionUID = 1L;
	public CommentCatException(String message) {
		super(message);
	}
	public CommentCatException(String message, Throwable cause) {
		super(message, cause);
	}
}
