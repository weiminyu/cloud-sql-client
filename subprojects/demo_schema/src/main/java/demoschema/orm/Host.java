package demoschema.orm;

import javax.persistence.Entity;
import javax.persistence.Id;

/** ORM entity model for DNS host. */
@Entity
public class Host {
  private String eppRepoId;
  private String fqhn;

  /** Default constructor for Hibernate. */
  public Host() {}

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
