package demoschema.orm;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** ORM entity model for DNS host. */
@Entity
@Table(name = "host")
public class HostEntity {
  private String eppRepoId;
  private String fqhn;

  /** Default constructor for Hibernate. */
  public HostEntity() {}

  @Id
  public String getEppRepoId() {
    return eppRepoId;
  }

  public void setEppRepoId(String eppRepoId) {
    this.eppRepoId = eppRepoId;
  }

  public String getFqhn() {
    return fqhn;
  }

  public void setFqhn(String fqhn) {
    this.fqhn = fqhn;
  }
}
