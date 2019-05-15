package demoschema.orm;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/** ORM entity model for DNS domain. */
@Entity
@Table(name = "domain")
public class DomainEntity {
  private String eppRepoId;
  private String fqdn;
  private String nonSchemaElement;

  /** Default constructor for Hibernate. */
  public DomainEntity() {}

  @Id
  public String getEppRepoId() {
    return eppRepoId;
  }

  public void setEppRepoId(String eppRepoId) {
    this.eppRepoId = eppRepoId;
  }

  String getFqdn() {
    return fqdn;
  }

  void setFqdn(String fqdn) {
    this.fqdn = fqdn;
  }

  @Transient
  String getNonSchemaElement() {
    return nonSchemaElement;
  }

  void setNonSchemaElement(String nonSchemaElement) {
    this.nonSchemaElement = nonSchemaElement;
  }
}
