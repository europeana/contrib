package gr.ntua.ivml.awareness.util;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = EuropeanaResourceValidator.class)
public @interface HasEuropeanaResource {
	   String message() default "{gr.ntua.ivml.awareness.util.HasEuropeanaResource}";

	    Class<?>[] groups() default {};

	    Class<? extends Payload>[] payload() default {};
}
