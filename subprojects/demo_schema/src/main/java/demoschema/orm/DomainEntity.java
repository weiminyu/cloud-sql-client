package demoschema.orm;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** ORM entity model for DNS domain. */
@Entity
@Table(name = "DOMAIN")
public class DomainEntity {
  private String eppRepoId;
  private String fqdn;

  /** Default constructor for Hibernate. */
  public DomainEntity() {}

  @Id
  public String getEppRepoId() {
    return eppRepoId;
  }

  public void setEppRepoId(String eppRepoId) {
    this.eppRepoId = eppRepoId;
  }

  public String getFqdn() {
    return fqdn;
  }

  public void setFqdn(String fqdn) {
    this.fqdn = fqdn;
  }
}
