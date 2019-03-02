A playground for experiments with CloudSQL and other ideas.

## Subprojects

### JDBC

This project provides CloudSQL JDBC support based on two popular connection
pools: [HikariCP](https://github.com/brettwooldridge/HikariCP) and
[Tomcat JDBC](https://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html).

It targets to use cases:

*   connections using CloudSQL's secure socket factories.
*   connections to a local CloudSQL proxy on a secure host.

In both cases security is managed by the CloudSQL utilities, and the
JDBC connections themselves have SSL disabled.

The connection pools work with both MYSQL and POSTGRESQL.

