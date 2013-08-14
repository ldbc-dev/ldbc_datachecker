LDBC Data Checker
-----------------

**Build**

mvn clean package -Dmaven.compiler.source=1.6 -Dmaven.compiler.target=1.6

**Run**

java -cp target/datachecker-0.1-SNAPSHOT.jar com.ldbc.datachecker.socialnet.SocialNetCheck PATH/TO/YOUR/SOCIAL_NET/DATA/DIRECTORY
