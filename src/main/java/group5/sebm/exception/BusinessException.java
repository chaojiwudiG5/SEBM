package group5.sebm.exception;

public class BusinessException extends Exception {

  private final int code;

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
