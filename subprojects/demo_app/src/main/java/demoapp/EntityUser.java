package demoapp;

import demoschema.orm.RegistryLock;

public class EntityUser {

  public static final demoschema.orm.RegistryLock LOCK = new RegistryLock("whatever");
}
