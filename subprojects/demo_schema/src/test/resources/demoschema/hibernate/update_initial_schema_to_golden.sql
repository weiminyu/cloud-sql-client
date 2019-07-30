
    create table "Host" (
       epp_repo_id  bigserial not null,
        fqhn varchar(255),
        primary key (epp_repo_id)
    );

    create table "RegistryLock" (
       repo_id  bigserial not null,
        revision_id bigserial not null,
        primary key (repo_id, revision_id)
    );
