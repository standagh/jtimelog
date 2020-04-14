package cz.hatua.jtimelog;

public class JTimeLogException extends Exception {
	static final long serialVersionUID = 1L;
    public JTimeLogException(String message) {
        super(message);
    }
    public JTimeLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
