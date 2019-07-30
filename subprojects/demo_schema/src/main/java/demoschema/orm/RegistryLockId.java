package demoschema.orm;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Unique identifier of {@link RegistryLock}. */
// TODO(weiminyu): mostly boiler-plate code. Use code generation.
public class RegistryLockId implements Serializable {

  private Long revisionId;

  private String repoId;

  public RegistryLockId() {}

  public RegistryLockId(String repoId, Long revisionId) {
    this.repoId = repoId;
    this.revisionId = revisionId;
  }

  // @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  // @Column(columnDefinition = "bigserial")
  // @NotPortable(
  //     details = {
  //         "bigserial is Postgres-speicific type for auto-incrementing int64",
  //         "GenerationType.IDENTITY assumes an auto-increment or identitiy column on table"
  //     })
  public Long getRevisionId() {
    return revisionId;
  }

  public void setRevisionId(Long revisionId) {
    this.revisionId = revisionId;
  }

  public String getRepoId() {
    return repoId;
  }

  public void setRepoId(String repoId) {
    this.repoId = repoId;
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
    return  Objects.equals(revisionId, that.revisionId) && Objects.equals(repoId, that.repoId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repoId, revisionId);
  }
}
