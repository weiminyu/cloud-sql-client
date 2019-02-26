package testutils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/** Class-level {@link Qualifier} for tests with injectable fields. */
@Qualifier
@Documented
@Target(ElementType.TYPE)
public @interface InjectableTest {}
