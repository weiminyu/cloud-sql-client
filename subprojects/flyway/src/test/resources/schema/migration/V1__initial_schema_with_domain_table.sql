-- See src/test/resources/demoschema/hibernate/initial_schema.sql
    create table domain (
       eppRepoId varchar(255) not null,
        fqdn varchar(255),
        primary key (eppRepoId)
    );
