package demoschema.orm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Transient;

@Entity
@IdClass(RegistryLockId.class)
public class RegistryLock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //@Column(columnDefinition = "bigserial")
  private Long revisionId;

  @Id
  @Column(columnDefinition = "varchar(255)")
  private String repoId;

  public RegistryLock() {}

  public RegistryLock(String repoId, Long revisionId) {
    this.repoId = repoId;
    this.revisionId = revisionId;
  }

  public RegistryLockId getId() {
    return new RegistryLockId(repoId, revisionId);
  }

  public void setId(RegistryLockId id) {
    this.repoId = id.getRepoId();
    this.revisionId = id.getRevisionId();
  }
}
