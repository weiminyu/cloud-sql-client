package demoschema.orm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.persistence.Entity;

/**
 * Declares the {@link Entity} properties that may be used to check for Idempotency. Such properties
 * have the following attributes:
 *
 * <ul>
 *   <li>Only have user-assigned values
 *   <li>Uniquely identify an {@link Entity}, at least most of the time.
 *   <li>Do not form a unique index.
 * </ul>
 *
 * <p>A mutation may use these properties to ensure idempotency, e.g., after the storage system
 * times out.
 *
 * <p>This annotation is informational only. However, compile-time checks may be introduced in the
 * future.
 */
@Retention(RetentionPolicy.CLASS)
public @interface IdempotencyCheck {

  String[] properties();

  /** True if {@link #properties()} do not uniquely an {@link Entity} every time. */
  boolean isBestEffort() default false;
}
