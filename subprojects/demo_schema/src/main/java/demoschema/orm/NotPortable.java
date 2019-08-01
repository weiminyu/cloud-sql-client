package demoschema.orm;

import demoschema.orm.NotPortable.NonPortables;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@Repeatable(value = NonPortables.class)
public @interface NotPortable {

  Cause cause();

  String details() default "";

  @interface NonPortables {
    NotPortable[] value();
  }

  enum Cause {
    DATABASE,
    ORM
  }
}
