package ist.meic.pa.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to test the method assertions
 * at method entry and method return.
 * At method entry, only the expressions that don't contain "$_" are evaluated.
 * The expressions that contain "$_" will be evaluated at method return.
 * 
 * @author group3
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR } ) 
public @interface ExtendedAssertion {
	String value();
}