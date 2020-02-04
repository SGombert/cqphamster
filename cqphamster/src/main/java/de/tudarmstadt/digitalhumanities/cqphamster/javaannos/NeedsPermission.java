package de.tudarmstadt.digitalhumanities.cqphamster.javaannos;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface NeedsPermission {
	public boolean singleUserPermissionsEnabled = true;
	public boolean userGroupPermissionsEnabled = true;
}
