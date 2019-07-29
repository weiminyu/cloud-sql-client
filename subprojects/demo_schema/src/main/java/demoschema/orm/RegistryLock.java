package demoschema.orm;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class RegistryLock {

  @EmbeddedId
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private RegistryLockId id;

  public RegistryLock() {}

  public RegistryLock(RegistryLockId id) {
    this.id = id;
  }

  public RegistryLockId getId() {
    return id;
  }

  public void setId(RegistryLockId id) {
    this.id = id;
  }
}
