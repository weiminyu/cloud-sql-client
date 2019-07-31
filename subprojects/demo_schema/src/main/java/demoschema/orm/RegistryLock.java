package demoschema.orm;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    // Unique constraint to get around Hibernate's failure to handle
    // auto-increment field in composite primary key.
    indexes =
        @Index(
            name = "idx_registry_lock_repo_id_revision_id",
            columnList = "repoId, revisionId",
            unique = true))
@Check(
    constraints =
        "locking_registrar_contact_id IS NOT NULL OR unlocking_registrar_contact_id IS NOT NULL")
@NonPortable(orm = true)
public class RegistryLock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long revisionId;

  @Column(nullable = false)
  private String repoId;

  // Optimistic lock.
  @Version private Integer transactionLock;

  @Column(nullable = false)
  private String domainName;

  @Column(nullable = false)
  private String registrarClientId;

  private String lockingRegistrarContactId;
  private String unlockingRegistrarContactId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LockAction lockAction;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LockStatus lockStatus;

  @CreationTimestamp
  @NonPortable(orm = true)
  @Column(nullable = false)
  private ZonedDateTime creationTimestamp;

  private ZonedDateTime lockTimestamp;

  private ZonedDateTime unlockTimestamp;

  @Column(nullable = false)
  private String verificationCode;

  private boolean isSuperUser;

  public RegistryLock() {}

  public RegistryLock(String repoId) {
    this.repoId = repoId;
  }

  public String getRepoId() {
    return repoId;
  }

  public void setRepoId(String repoId) {
    this.repoId = repoId;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public String getRegistrarClientId() {
    return registrarClientId;
  }

  public void setRegistrarClientId(String registrarClientId) {
    this.registrarClientId = registrarClientId;
  }

  public String getLockingRegistrarContactId() {
    return lockingRegistrarContactId;
  }

  public void setLockingRegistrarContactId(String lockingRegistrarContactId) {
    this.lockingRegistrarContactId = lockingRegistrarContactId;
  }

  public String getUnlockingRegistrarContactId() {
    return unlockingRegistrarContactId;
  }

  public void setUnlockingRegistrarContactId(String unlockingRegistrarContactId) {
    this.unlockingRegistrarContactId = unlockingRegistrarContactId;
  }

  public LockAction getLockAction() {
    return lockAction;
  }

  public void setLockAction(LockAction lockAction) {
    this.lockAction = lockAction;
  }

  public ZonedDateTime getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(ZonedDateTime creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public ZonedDateTime getLockTimestamp() {
    return lockTimestamp;
  }

  public void setLockTimestamp(ZonedDateTime lockTimestamp) {
    this.lockTimestamp = lockTimestamp;
  }

  public ZonedDateTime getUnlockTimestamp() {
    return unlockTimestamp;
  }

  public void setUnlockTimestamp(ZonedDateTime unlockTimestamp) {
    this.unlockTimestamp = unlockTimestamp;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }

  public boolean isSuperUser() {
    return isSuperUser;
  }

  public void setSuperUser(boolean superUser) {
    isSuperUser = superUser;
  }

  public Long getRevisionId() {
    return revisionId;
  }

  public void setRevisionId(Long revisionId) {
    this.revisionId = revisionId;
  }

  public LockStatus getLockStatus() {
    return lockStatus;
  }

  public void setLockStatus(LockStatus lockStatus) {
    this.lockStatus = lockStatus;
  }

  public Integer getTransactionLock() {
    return transactionLock;
  }

  public void setTransactionLock(Integer transactionLock) {
    this.transactionLock = transactionLock;
  }
}
