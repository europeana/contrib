package pt.utl.ist.repox.web.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;





/**
 *
 * @author Georg Petz
 */
@Documented
@Constraint(validatedBy = HomepageValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Homepage {
    
    public abstract String message() default "homepage does not exist";

    public abstract Class[] groups() default {};
    public abstract Class[] payload() default {};
}
