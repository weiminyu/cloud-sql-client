package demoschema.orm;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Unique identifier of {@link RegistryLock}. */
// TODO(weiminyu): mostly boiler-plate code. Use code generation.
@Embeddable
public class RegistryLockId implements Serializable {

  private String repoId;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "bigserial")
  @NotPortable(
      details = {
        "bigserial is Postgres-speicific type for auto-incrementing int64",
        "GenerationType.IDENTITY assumes an auto-increment or identitiy column on table"
      })
  private long revisionId = Long.MIN_VALUE;

  public RegistryLockId() {}

  public RegistryLockId(String repoId) {
    this.repoId = repoId;
  }

  public String getRepoId() {
    return repoId;
  }

  public void setRepoId(String repoId) {
    this.repoId = repoId;
  }

  public long getRevisionId() {
    return revisionId;
  }

  public void setRevisionId(long revisionId) {
    this.revisionId = revisionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegistryLockId that = (RegistryLockId) o;
    return revisionId == that.revisionId && Objects.equals(repoId, that.repoId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repoId, revisionId);
  }
}
