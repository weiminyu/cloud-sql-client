package demoschema.orm;

import demoschema.orm.NonPortable.NonPortables;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@Repeatable(value = NonPortables.class)
public @interface NonPortable {

  boolean database() default false;

  boolean orm() default false;

  String details() default "";

  @interface NonPortables {
    NonPortable[] value();
  }
}
