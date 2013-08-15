LDBC Data Checker
-----------------

**Build**

mvn clean package -Dmaven.compiler.source=1.6 -Dmaven.compiler.target=1.6

**Run**

    java -cp target/datachecker-0.1-SNAPSHOT.jar com.ldbc.datachecker.socialnet.SocialNetCheck SOCIAL_NET_DBGEN/DIR TERMINATE

Where:

 * `SOCIAL_NET_DBGEN/DIR`: is the top level directory of the [ldbc_socialnet_dbgen](https://github.com/ldbc/ldbc_socialnet_bm/tree/master/ldbc_socialnet_dbgen) project
    * Likely looks something like `...ldbc_socialnet_bm/ldbc_socialnet_dbgen/`
 * `TERMINATE`: specifies how `datachecker` should behave when an invalid data is encountered
    * `true` --> throw an exception with descriptive error message
    * `false` --> log a descriptive error message and continue
