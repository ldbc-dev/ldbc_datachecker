package com.ldbc.datachecker.socialnet;

import static com.ldbc.datachecker.Column.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.ldbc.datachecker.Check;
import com.ldbc.datachecker.CheckRunner;
import com.ldbc.datachecker.ColumnRef;
import com.ldbc.datachecker.DirectoryCheck;
import com.ldbc.datachecker.FailedCheckPolicy;
import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.checks.directory.DirectoryContainsAllAndOnlyExpectedCsvFiles;
import com.ldbc.datachecker.checks.file.ExpectedColumns;
import com.ldbc.datachecker.checks.file.ExpectedLength;
import com.ldbc.datachecker.failure.LoggingFailedCheckPolicy;
import com.ldbc.datachecker.failure.TerminateFailedCheckPolicy;

public class SocialNetCheck implements Check
{
    private static final Logger logger = Logger.getLogger( SocialNetCheck.class );

    private static final String DIR = "dir";
    private static final String TERMINATE = "terminate";
    private static final String LOG = "log";

    public static void main( String[] args )
    {
        Map<String, String> params = null;
        Options options = buildOptions();
        try
        {
            params = parseArgs( args, options );
        }
        catch ( ParseException e )
        {
            logger.error( e.getMessage() );
            System.out.println();

            int printedRowWidth = 110;
            String header = "";
            String footer = "";
            int spacesBeforeOption = 3;
            int spacesBeforeOptionDescription = 5;
            boolean displayUsage = true;
            printHelp( options, printedRowWidth, header, footer, spacesBeforeOption, spacesBeforeOptionDescription,
                    displayUsage, System.out );
            System.out.println();
            return;
        }

        logger.info( "LDBC Social Network Data Checker" );

        /*
         * ldbc_socialnet_bm_dbgen directory, e.g.:
         * "/home/alex/workspace/java/ldbc_socialnet_bm/ldbc_socialnet_dbgen/"
         */
        File dataGenDirectory = new File( params.get( DIR ) );
        File dataDirectory = new File( dataGenDirectory, "outputDir/" );
        Properties dataGenProperties = new Properties();
        try
        {
            dataGenProperties.load( new FileInputStream( new File( dataGenDirectory, "params.ini" ) ) );
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage() );
            return;
        }
        long personCount = Long.parseLong( (String) dataGenProperties.get( "numtotalUser" ) );
        logger.info( String.format( "Expected Person Count = %s", personCount ) );

        /*
         * terminate on error
         */
        boolean terminateOnError = Boolean.parseBoolean( params.get( TERMINATE ) );

        /*
         * log to file (as well as console)
         */
        boolean logToFile = Boolean.parseBoolean( params.get( LOG ) );

        FailedCheckPolicy policy = null;
        if ( true == terminateOnError )
        {
            policy = new TerminateFailedCheckPolicy();
        }
        else
        {
            Logger fileLogger = null;
            if ( true == logToFile )
            {
                fileLogger = Logger.getLogger( "file" );
                // RollingFileAppender appender = ( (RollingFileAppender)
                // fileLogger.getAppender( "file" ) );
                // appender.setFile( logFile );
                // appender.activateOptions();
            }
            policy = LoggingFailedCheckPolicy.toConsoleAndFile( logger, fileLogger );

        }

        // TODO 1
        long idsShouldIncrementBy = 10;

        try
        {
            Check socialNetCheck = new SocialNetCheck( dataDirectory, idsShouldIncrementBy, personCount );
            CheckRunner checkRunner = new CheckRunner( dataDirectory, socialNetCheck, policy );
            checkRunner.check();
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage() );
            return;
        }

        logger.info( "Check complete" );
    }

    private static Options buildOptions()
    {
        Option dataDirOption = OptionBuilder.isRequired().hasArg().withLongOpt( "dir" ).withDescription(
                "ldbc_socialnet_dbgen directory path" ).create( "d" );
        Option terminateOption = OptionBuilder.withLongOpt( "terminate" ).withDescription( "Terminate on error" ).create(
                "t" );
        Option logToFileOption = OptionBuilder.withLongOpt( "log" ).withDescription( "Log errors to csv file" ).create(
                "l" );

        Options options = new Options();
        options.addOption( dataDirOption );
        options.addOption( terminateOption );
        options.addOption( logToFileOption );

        return options;
    }

    private static Map<String, String> parseArgs( String[] args, Options options ) throws ParseException
    {
        Map<String, String> params = new HashMap<String, String>();

        CommandLineParser parser = new BasicParser();

        CommandLine cmd = parser.parse( options, args );

        params.put( DIR, cmd.getOptionValue( 'd' ) );
        params.put( TERMINATE, Boolean.toString( cmd.hasOption( 't' ) ) );
        params.put( LOG, Boolean.toString( cmd.hasOption( 'l' ) ) );

        return params;
    }

    public static void printUsage( final String applicationName, int printedRowWidth, final Options options,
            final OutputStream out )
    {
        final PrintWriter writer = new PrintWriter( System.out );
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage( writer, printedRowWidth, applicationName, options );
        writer.flush();
    }

    public static void printHelp( final Options options, final int printedRowWidth, final String header,
            final String footer, final int spacesBeforeOption, final int spacesBeforeOptionDescription,
            final boolean displayUsage, final OutputStream out )
    {
        final String commandLineSyntax = "java -cp datachecker-0.1-SNAPSHOT.jar " + SocialNetCheck.class.getName();
        final PrintWriter writer = new PrintWriter( out );
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp( writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption,
                spacesBeforeOptionDescription, footer, displayUsage );
        writer.flush();
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

        ColumnRef<Long> commentsRef = new ColumnRef.LongColumnRef( "comments" );

        // id|creationDate|locationIP|browserUsed|content
        fileChecks.add( new ExpectedColumns( inDir( "comment.csv" ),
                isLong().withConsecutive( 0l, idsShouldIncrementBy ).saveRefTo( commentsRef ),
                isDate( SocialNet.dateTimeFormat() ), isString().withRegex( SocialNet.locationIpRegex() ),
                isFiniteSet( SocialNet.browsers() ), isString() ) );

        ColumnRef<Long> forumsRef = new ColumnRef.LongColumnRef( "forums" );

        // id|title|creationDate
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "forum.csv" ), isLong().saveRefTo( forumsRef ), isString(),
                isDate( SocialNet.dateTimeFormat() ) ) );

        ColumnRef<Long> organisationsRef = new ColumnRef.LongColumnRef( "organisations" );

        // id|type|name|url
        // TODO url = isUrl()
        fileChecks.add( new ExpectedColumns( inDir( "organisation.csv" ), isLong().withConsecutive( 0l,
                idsShouldIncrementBy ).saveRefTo( organisationsRef ), isFiniteSet( SocialNet.organisationTypes() ),
                isString(), isString() ) );

        ColumnRef<Long> personsRef = new ColumnRef.LongColumnRef( "persons" );

        // id|firstName|lastName|gender|birthday|creationDate|locationIP|browserUsed
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "person.csv" ), isLong().saveRefTo( personsRef ), isString(),
                isString(), isFiniteSet( SocialNet.genders() ), isDate( SocialNet.dateFormat() ),
                isDate( SocialNet.dateTimeFormat() ), isString().withRegex( SocialNet.locationIpRegex() ),
                isFiniteSet( SocialNet.browsers() ) ) );

        // start at line 1 instead of 0 - don't count headers
        int startLine = 1;
        fileChecks.add( new ExpectedLength( inDir( "person.csv" ), startLine, personCount ) );

        ColumnRef<Long> placesRef = new ColumnRef.LongColumnRef( "places" );

        // id|name|url|type
        // TODO url = isUrl()
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "place.csv" ), isLong().saveRefTo( placesRef ), isString(),
                isString(), isFiniteSet( SocialNet.placeTypes() ) ) );

        ColumnRef<Long> postsRef = new ColumnRef.LongColumnRef( "posts" );

        // id|imageFile|creationDate|locationIP|browserUsed|language|content
        boolean imageIsOptional = true;
        fileChecks.add( new ExpectedColumns( inDir( "post.csv" ),
                isLong().withConsecutive( 0l, idsShouldIncrementBy ).saveRefTo( postsRef ), isString().withRegex(
                        SocialNet.imageFileRegex( imageIsOptional ) ), isDate( SocialNet.dateTimeFormat() ),
                isString().withRegex( SocialNet.locationIpRegex() ), isFiniteSet( SocialNet.browsers() ),
                isFiniteSet( SocialNet.languages( true ) ), isString() ) );

        ColumnRef<Long> tagclassesRef = new ColumnRef.LongColumnRef( "tagclasses" );

        // id|name|url
        // TODO url = isUrl()
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "tagclass.csv" ), isLong().saveRefTo( tagclassesRef ), isString(),
                isString() ) );

        ColumnRef<Long> tagsRef = new ColumnRef.LongColumnRef( "tags" );

        // id|name|url
        // TODO url = isUrl()
        // TODO id = isLong().withConsecutive( 0l, idsShouldIncrementBy )
        fileChecks.add( new ExpectedColumns( inDir( "tag.csv" ), isLong().saveRefTo( tagsRef ), isString(), isString() ) );

        /*
        * Relationships
        */

        // Comment.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_hasCreator_person.csv" ),
                isLong().checkRefIn( commentsRef ), isLong().checkRefIn( personsRef ) ) );

        // Comment.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_isLocatedIn_place.csv" ),
                isLong().checkRefIn( commentsRef ), isLong().checkRefIn( placesRef ) ) );

        // Comment.id|Comment.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_replyOf_comment.csv" ),
                isLong().checkRefIn( commentsRef ), isLong().checkRefIn( commentsRef ) ) );

        // Comment.id|Post.id
        fileChecks.add( new ExpectedColumns( inDir( "comment_replyOf_post.csv" ), isLong().checkRefIn( commentsRef ),
                isLong().checkRefIn( postsRef ) ) );

        // Forum.id|Post.id
        fileChecks.add( new ExpectedColumns( inDir( "forum_containerOf_post.csv" ), isLong().checkRefIn( forumsRef ),
                isLong().checkRefIn( postsRef ) ) );

        // Forum.id|Person.id|joinDate
        fileChecks.add( new ExpectedColumns( inDir( "forum_hasMember_person.csv" ), isLong().checkRefIn( forumsRef ),
                isLong().checkRefIn( personsRef ), isDate( SocialNet.dateTimeFormat() ) ) );

        // Forum.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "forum_hasModerator_person.csv" ),
                isLong().checkRefIn( forumsRef ), isLong().checkRefIn( personsRef ) ) );

        // Forum.id|Tag.id
        fileChecks.add( new ExpectedColumns( inDir( "forum_hasTag_tag.csv" ), isLong().checkRefIn( forumsRef ),
                isLong().checkRefIn( tagsRef ) ) );

        // Person.id|email
        fileChecks.add( new ExpectedColumns( inDir( "person_email_emailaddress.csv" ),
                isLong().checkRefIn( personsRef ), isEmailAddress() ) );

        // Person.id|Tag.id
        fileChecks.add( new ExpectedColumns( inDir( "person_hasInterest_tag.csv" ), isLong().checkRefIn( personsRef ),
                isLong().checkRefIn( tagsRef ) ) );

        // Person.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "person_isLocatedIn_place.csv" ),
                isLong().checkRefIn( personsRef ), isLong().checkRefIn( placesRef ) ) );

        // Person.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "person_knows_person.csv" ), isLong().checkRefIn( personsRef ),
                isLong().checkRefIn( personsRef ) ) );

        // Person.id|Post.id|creationDate
        fileChecks.add( new ExpectedColumns( inDir( "person_likes_post.csv" ), isLong().checkRefIn( personsRef ),
                isLong().checkRefIn( postsRef ), isDate( SocialNet.dateTimeFormat() ) ) );

        // Person.id|language
        fileChecks.add( new ExpectedColumns( inDir( "person_speaks_language.csv" ), isLong().checkRefIn( personsRef ),
                isFiniteSet( SocialNet.languages() ) ) );

        // Person.id|Organisation.id|classYear
        fileChecks.add( new ExpectedColumns( inDir( "person_studyAt_organisation.csv" ), isLong().checkRefIn(
                personsRef ), isLong().checkRefIn( organisationsRef ), isInteger() ) );

        // Person.id|Organisation.id|workFrom
        fileChecks.add( new ExpectedColumns( inDir( "person_workAt_organisation.csv" ),
                isLong().checkRefIn( personsRef ), isLong().checkRefIn( organisationsRef ), isInteger() ) );

        // Place.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "place_isPartOf_place.csv" ), isLong().checkRefIn( placesRef ),
                isLong().checkRefIn( placesRef ) ) );

        // Post.id|Person.id
        fileChecks.add( new ExpectedColumns( inDir( "post_hasCreator_person.csv" ), isLong().checkRefIn( postsRef ),
                isLong().checkRefIn( personsRef ) ) );

        // Post.id|Tag.id
        fileChecks.add( new ExpectedColumns( inDir( "post_hasTag_tag.csv" ), isLong().checkRefIn( postsRef ),
                isLong().checkRefIn( tagsRef ) ) );

        // Post.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "post_isLocatedIn_place.csv" ), isLong().checkRefIn( postsRef ),
                isLong().checkRefIn( placesRef ) ) );

        // TagClass.id|TagClass.id
        fileChecks.add( new ExpectedColumns( inDir( "tagclass_isSubclassOf_tagclass.csv" ), isLong().checkRefIn(
                tagclassesRef ), isLong().checkRefIn( tagclassesRef ) ) );

        // Tag.id|TagClass.id
        fileChecks.add( new ExpectedColumns( inDir( "tag_hasType_tagclass.csv" ), isLong().checkRefIn( tagsRef ),
                isLong().checkRefIn( tagclassesRef ) ) );

        // Organisation.id|Place.id
        fileChecks.add( new ExpectedColumns( inDir( "organisation_isLocatedIn_place.csv" ), isLong().checkRefIn(
                organisationsRef ), isLong().checkRefIn( placesRef ) ) );

        return fileChecks;
    }

    private String inDir( String filename )
    {
        return dataDirectory.getAbsolutePath() + "/" + filename;
    }
}
