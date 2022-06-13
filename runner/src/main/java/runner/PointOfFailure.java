package runner;

import java.io.Serializable;

/**
 * Represents the source code location where a test failed.
 */
public class PointOfFailure implements Serializable {

    private static final long serialVersionUID = 3845766554231113632L;

    private final String className;
    private final int lineNumber;
    private final String errorClassName;
    private final String message;

    public PointOfFailure(String className, int lineNumber, String errorClassName, String message) {
        this.className = removeInnerClasses(className);
        this.lineNumber = lineNumber;
        this.errorClassName = errorClassName;
        this.message = message;
    }

    private String removeInnerClasses(String className) {
        return className.split("\\$")[0];
    }

    public String getMessage() {
        return message;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int hashCode() {
        int hashCode = className.hashCode() ^ lineNumber ^ errorClassName.hashCode();
        if (message != null) {
            hashCode = hashCode ^ message.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PointOfFailure)) {
            return false;
        }

        PointOfFailure that = (PointOfFailure) other;
        return className.equals(that.className) &&
            (lineNumber == that.lineNumber) &&
            errorClassName.equals(that.errorClassName) &&
            safeEquals(message, that.message);
    }

    private boolean safeEquals(Object orig, Object other) {
        if ((orig == null) && (other == null)) {
            return true;
        }
        if ((orig == null) || (other == null)) {
            return false;
        }

        return orig.equals(other);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(className).append(':').append(lineNumber).append(" - ").append(errorClassName);
        if (message != null) {
            builder.append('(').append(message).append(')');
        }
        return builder.toString();
    }
}
