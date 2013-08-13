package com.ldbc.datachecker.socialnet;

import static com.ldbc.datachecker.checks.file.Column.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ldbc.datachecker.Check;
import com.ldbc.datachecker.CheckResult;
import com.ldbc.datachecker.CheckRunner;
import com.ldbc.datachecker.DirectoryCheck;
import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.checks.directory.DirectoryContainsAllAndOnlyExpectedCsvFiles;
import com.ldbc.datachecker.checks.directory.IsDirectory;
import com.ldbc.datachecker.checks.file.ExpectedColumns;
import com.ldbc.datachecker.checks.file.ExpectedLength;

public class SocialNetCheck implements Check
{
    /*
     * Graph
     *  TODO dangling relationship check
     */

    public static void main( String[] args )
    {
        File dataDirectory = new File( "/home/alex/workspace/java/ldbc_socialnet_bm/ldbc_socialnet_dbgen/outputDir/" );
        // TODO 1l
        long idsShouldIncrementBy = 10;
        long personCount = 5000;

        Check socialNetCheck = new SocialNetCheck( dataDirectory, idsShouldIncrementBy, personCount );
        CheckRunner directoryChecker = new CheckRunner( dataDirectory, socialNetCheck );
        CheckResult result = directoryChecker.check();
        System.out.println( result );
    }

    private final File dataDirectory;
    private final long idsShouldIncrementBy;
    private final long personCount;

    public SocialNetCheck( File dataDirectory, long idsShouldIncrementBy, long personCount )
    {
        this.dataDirectory = dataDirectory;
        this.idsShouldIncrementBy = idsShouldIncrementBy;
        this.personCount = personCount;
    }

    @Override
    public List<DirectoryCheck> getDirectoryChecks()
    {
        List<DirectoryCheck> directoryChecks = new ArrayList<DirectoryCheck>();
        directoryChecks.add( new IsDirectory() );
        directoryChecks.add( new DirectoryContainsAllAndOnlyExpectedCsvFiles( SocialNet.allCsvFilenames( dataDirectory ) ) );
        return directoryChecks;
    }

    @Override
    public List<FileCheck> getFileChecks()
    {
        List<FileCheck> fileChecks = new ArrayList<FileCheck>();

        /*
         * Nodes
         */

        // id|creationDate|locationIP|browserUsed|content
        fileChecks.add( new ExpectedColumns( inDir( "comment.csv" ),
                isLong().withConsecutive( 0l, idsShouldIncrementBy ), isDate( SocialNet.dateTimeFormat() ),
                isString().withRegex( SocialNet.locationIpRegex() ), isFiniteSet( SocialNet.browsers() ), isString() ) );

        // id|title|creationDate
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "forum.csv" ), isLong(), isString(),
                isDate( SocialNet.dateTimeFormat() ) ) );

        // id|type|name|url
        // TODO url = isUrl()
        fileChecks.add( new ExpectedColumns( inDir( "organisation.csv" ), isLong().withConsecutive( 0l,
                idsShouldIncrementBy ), isFiniteSet( SocialNet.organisationTypes() ), isString(), isString() ) );

        // id|firstName|lastName|gender|birthday|creationDate|locationIP|browserUsed
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "person.csv" ), isLong(), isString(), isString(),
                isFiniteSet( SocialNet.genders() ), isDate( SocialNet.dateFormat() ),
                isDate( SocialNet.dateTimeFormat() ), isString().withRegex( SocialNet.locationIpRegex() ),
                isFiniteSet( SocialNet.browsers() ) ) );

        // start at line 1 instead of 0 - don't count headers
        int startLine = 1;
        fileChecks.add( new ExpectedLength( inDir( "person.csv" ), startLine, personCount ) );

        // id|name|url|type
        // TODO url = isUrl()
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "place.csv" ), isLong(), isString(), isString(),
                isFiniteSet( SocialNet.placeTypes() ) ) );

        // id|imageFile|creationDate|locationIP|browserUsed|language|content
        // TODO languages needed
        fileChecks.add( new ExpectedColumns( inDir( "post.csv" ), isLong().withConsecutive( 0l, idsShouldIncrementBy ),
                isString().withRegex( SocialNet.imageFileRegex() ), isDate( SocialNet.dateTimeFormat() ),
                isString().withRegex( SocialNet.locationIpRegex() ), isFiniteSet( SocialNet.browsers() ), isString(),
                isString() ) );

        // id|name|url
        // TODO url = isUrl()
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "tagclass.csv" ), isLong(), isString(), isString() ) );

        // id|name|url
        // TODO url = isUrl()
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "tag.csv" ), isLong(), isString(), isString() ) );

        /*
        * Relationships
        */

        // Comment.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_hasCreator_person.csv" ), isLong(), isLong() ) );

        // Comment.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_isLocatedIn_place.csv" ), isLong(), isLong() ) );

        // Comment.id|Comment.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_replyOf_comment.csv" ), isLong(), isLong() ) );

        // Comment.id|Post.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_replyOf_post.csv" ), isLong(), isLong() ) );

        // Forum.id|Post.id
        fileChecks.add( new ExpectedColumns( inDir( "forum_containerOf_post.csv" ), isLong(), isLong() ) );

        // Forum.id|Person.id|joinDate
        fileChecks.add( new ExpectedColumns( inDir( "forum_hasMember_person.csv" ), isLong(), isLong(),
                isDate( SocialNet.dateTimeFormat() ) ) );

        // Forum.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "forum_hasModerator_person.csv" ), isLong(), isLong() ) );

        // Forum.id|Tag.id
        fileChecks.add( new ExpectedColumns( inDir( "forum_hasTag_tag.csv" ), isLong(), isLong() ) );

        // Person.id|email
        // TODO email = isString().withAccents( false ).withRegex(
        // SocialNet.emailAddressRegex() )
        fileChecks.add( new ExpectedColumns( inDir( "person_email_emailaddress.csv" ), isLong(),
                isString().withAccents( false ).withRegex( SocialNet.emailAddressRegex() ) ) );

        // Person.id|Tag.id
        fileChecks.add( new ExpectedColumns( inDir( "person_hasInterest_tag.csv" ), isLong(), isLong() ) );

        // Person.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "person_isLocatedIn_place.csv" ), isLong(), isLong() ) );

        // Person.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "person_knows_person.csv" ), isLong(), isLong() ) );

        // Person.id|Post.id|creationDate
        fileChecks.add( new ExpectedColumns( inDir( "person_likes_post.csv" ), isLong(), isLong(),
                isDate( SocialNet.dateTimeFormat() ) ) );

        // Person.id|language
        fileChecks.add( new ExpectedColumns( inDir( "person_speaks_language.csv" ), isLong(),
                isFiniteSet( SocialNet.languages() ) ) );

        // Person.id|Organisation.id|classYear
        fileChecks.add( new ExpectedColumns( inDir( "person_studyAt_organisation.csv" ), isLong(), isLong(),
                isInteger() ) );

        // Person.id|Organisation.id|workFrom
        fileChecks.add( new ExpectedColumns( inDir( "person_workAt_organisation.csv" ), isLong(), isLong(), isInteger() ) );

        // Place.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "place_isPartOf_place.csv" ), isLong(), isLong() ) );

        // Post.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "post_hasCreator_person.csv" ), isLong(), isLong() ) );

        // Post.id|Tag.id
        fileChecks.add( new ExpectedColumns( inDir( "post_hasTag_tag.csv" ), isLong(), isLong() ) );

        // Post.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "post_isLocatedIn_place.csv" ), isLong(), isLong() ) );

        // TagClass.id|TagClass.id
        fileChecks.add( new ExpectedColumns( inDir( "tagclass_isSubclassOf_tagclass.csv" ), isLong(), isLong() ) );

        // Tag.id|TagClass.id
        fileChecks.add( new ExpectedColumns( inDir( "tag_hasType_tagclass.csv" ), isLong(), isLong() ) );

        // Organisation.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "organisation_isLocatedIn_place.csv" ), isLong(), isLong() ) );

        return fileChecks;
    }

    private String inDir( String filename )
    {
        return dataDirectory.getAbsolutePath() + "/" + filename;
    }
}
