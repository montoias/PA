package ist.meic.pa.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Standard annotation asked in the project description.
 * @author group3
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR } ) 
public @interface Assertion {
	String value();
}