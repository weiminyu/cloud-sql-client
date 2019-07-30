package demoschema.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** ORM entity model for DNS host. */
@Entity
public class Host {
  private Long eppRepoId;
  private String fqhn = "unassigned";

  /** Default constructor for Hibernate. */
  public Host() {}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getEppRepoId() {
    return eppRepoId;
  }

  public void setEppRepoId(Long eppRepoId) {
    this.eppRepoId = eppRepoId;
  }

  public String getFqhn() {
    return fqhn;
  }

  public void setFqhn(String fqhn) {
    this.fqhn = fqhn;
  }
}
