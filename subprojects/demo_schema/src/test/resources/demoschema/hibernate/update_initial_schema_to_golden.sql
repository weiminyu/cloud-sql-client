
    create table "Host" (
       epp_repo_id varchar(255) not null,
        fqhn varchar(255),
        primary key (epp_repo_id)
    );

    create table "RegistryLock" (
       repo_id varchar(255) not null,
        revision_id bigserial not null,
        primary key (repo_id, revision_id)
    );
