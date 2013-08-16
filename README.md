LDBC Data Checker
-----------------

**Build**

    mvn clean package -Dmaven.compiler.source=1.6 -Dmaven.compiler.target=1.6

**Run**

    java -cp target/datachecker-0.1-SNAPSHOT.jar com.ldbc.datachecker.socialnet.SocialNetCheck DBGEN_DIR TERMINATE LOG_TO_CSV

Where:

 * `DBGEN_DIR`: is the top level directory of the [ldbc_socialnet_dbgen](https://github.com/ldbc/ldbc_socialnet_bm/tree/master/ldbc_socialnet_dbgen) project
    * It likely looks something like `...ldbc_socialnet_bm/ldbc_socialnet_dbgen/`
 * `TERMINATE`: specifies how `datachecker` should behave when an invalid data is encountered
    * `true` --> throw an exception with descriptive error message
    * `false` --> log a descriptive error message
 * `LOG_TO_CSV`: (only used if `TERMINATE == false`) specifies if errors should be written to `validation_errors.csv`
    * `true` --> `datachecker` logs errors to, both, console and `validation_errors.csv`
    * `false` --> `datachecker` logs errors to console only

