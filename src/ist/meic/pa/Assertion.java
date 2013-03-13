package ist.meic.pa;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD } ) 
public @interface Assertion {

	String value();

}