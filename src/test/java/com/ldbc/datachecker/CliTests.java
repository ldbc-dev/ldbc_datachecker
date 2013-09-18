package com.ldbc.datachecker;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class CliTests
{
    @Test
    public void shouldSupportOptionsWithMultipleValueAssignments() throws ParseException
    {
        Options options = new Options();

        Option valueOption = OptionBuilder.hasArgs( 1 ).withArgName( "value" ).create( "x" );
        options.addOption( valueOption );

        String[] args = { "-x", "1", "-x", "2" };

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse( options, args );

        assertThat( cmd.getOptionProperties( "x" ).size(), is( 2 ) );
        assertThat( cmd.getOptionProperties( "x" ).containsKey( "1" ), is( true ) );
        assertThat( cmd.getOptionProperties( "x" ).containsKey( "2" ), is( true ) );
    }

    @Test
    public void shouldSupportKeyValueOptionsWithMultipleValueAssignments() throws ParseException
    {
        Options options = new Options();

        Option propertyOption = OptionBuilder.hasArgs( 2 ).withValueSeparator( '=' ).withArgName( "key=value" ).withDescription(
                "key value" ).create( "p" );
        options.addOption( propertyOption );

        String[] args = { "-p", "a=1", "-p", "b=3" };

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse( options, args );

        assertThat( Integer.parseInt( (String) cmd.getOptionProperties( "p" ).get( "a" ) ), is( 1 ) );
        assertThat( Integer.parseInt( (String) cmd.getOptionProperties( "p" ).get( "b" ) ), is( 3 ) );
    }

    @Test
    public void shouldSupportMultiValuedOptions() throws ParseException
    {
        Options options = new Options();

        Option propertyOption = OptionBuilder.hasArgs().withValueSeparator( ':' ).withArgName( "item1:item2" ).withDescription(
                "list of things" ).create( "l" );
        options.addOption( propertyOption );

        String[] args = { "-l", "a:b:c:d", "-l", "e:f" };

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse( options, args );

        assertThat( cmd.getOptionValues( "l" ), is( new String[] { "a", "b", "c", "d", "e", "f" } ) );
    }

    @Test
    public void optionGroupShouldBeExclusive() throws ParseException
    {
        Options options = new Options();

        Option a = OptionBuilder.create( "a" );
        Option b = OptionBuilder.create( "b" );

        OptionGroup group = new OptionGroup();
        group.addOption( a );
        group.addOption( b );

        options.addOptionGroup( group );

        String[] args = { "-a", "-b" };

        boolean exceptionThrown = false;
        CommandLineParser parser = new BasicParser();
        try
        {
            parser.parse( options, args );
        }
        catch ( AlreadySelectedException e )
        {
            exceptionThrown = true;
        }

        assertThat( exceptionThrown, is( true ) );
    }

    @Test
    public void shouldOnlyPassIfRequiredOptionGiven() throws ParseException
    {
        Options options = new Options();
        Option required = OptionBuilder.isRequired().hasArg().withLongOpt( "required" ).create( "r" );
        options.addOption( required );
        CommandLineParser parser = new BasicParser();

        String[] longPassArgs = { "-required", "1" };
        String[] shortPassArgs = { "-r", "1" };
        String[] noValueArgs = { "-r" };
        String[] nothingArgs = {};
        String[] failArgs = { "-x", "1" };

        CommandLine cmd = null;

        cmd = parser.parse( options, longPassArgs );
        assertThat( cmd.hasOption( "r" ), is( true ) );
        assertThat( cmd.hasOption( "required" ), is( true ) );

        cmd = parser.parse( options, shortPassArgs );
        assertThat( cmd.hasOption( "r" ), is( true ) );
        assertThat( cmd.hasOption( "required" ), is( true ) );

        boolean missingArg = false;
        try
        {
            cmd = parser.parse( options, noValueArgs );
        }
        catch ( MissingArgumentException e )
        {
            missingArg = true;
        }
        assertThat( missingArg, is( true ) );

        boolean missingOption = false;
        try
        {
            cmd = parser.parse( options, nothingArgs );
        }
        catch ( MissingOptionException e )
        {
            missingOption = true;
        }
        assertThat( missingOption, is( true ) );

        boolean unrecognizedOperation = false;
        try
        {
            cmd = parser.parse( options, failArgs );
        }
        catch ( UnrecognizedOptionException e )
        {
            unrecognizedOperation = true;
        }
        assertThat( unrecognizedOperation, is( true ) );
    }

    @Test
    public void shouldOnlyPassWithCorrectArgumentCount() throws ParseException
    {
        Options options = new Options();
        Option property = OptionBuilder.hasArgs( 2 ).withValueSeparator( '=' ).withLongOpt( "property" ).create( "p" );
        options.addOption( property );
        CommandLineParser parser = new BasicParser();

        String[] passLong = { "-property", "x=1" };
        String[] passShort = { "-p", "x=1" };

        String[] passButTooManyArgs = { "-p", "x=1=2" };
        String[] passButTooFewArgs = { "-p", "x" };
        String[] passButNoOption = {};

        String[] failNoArgs = { "-p" };
        String[] failIllegalArg = { "-x", "1" };

        CommandLine cmd = null;

        cmd = parser.parse( options, passLong );
        assertThat( cmd.hasOption( "p" ), is( true ) );
        assertThat( cmd.hasOption( "property" ), is( true ) );
        assertThat( cmd.getOptionValues( "p" ).length, is( 2 ) );
        assertThat( cmd.getOptionValues( "p" )[0], is( "x" ) );
        assertThat( cmd.getOptionValues( "p" )[1], is( "1" ) );

        cmd = parser.parse( options, passShort );
        assertThat( cmd.hasOption( "p" ), is( true ) );
        assertThat( cmd.hasOption( "property" ), is( true ) );
        assertThat( cmd.getOptionValues( "p" ).length, is( 2 ) );
        assertThat( cmd.getOptionValues( "p" )[0], is( "x" ) );
        assertThat( cmd.getOptionValues( "p" )[1], is( "1" ) );

        cmd = parser.parse( options, passButTooManyArgs );
        assertThat( cmd.hasOption( "p" ), is( true ) );
        assertThat( cmd.hasOption( "property" ), is( true ) );
        assertThat( cmd.getOptionValues( "p" ).length, is( 2 ) );
        assertThat( cmd.getOptionValues( "p" )[0], is( "x" ) );
        assertThat( cmd.getOptionValues( "p" )[1], is( "1=2" ) );

        cmd = parser.parse( options, passButTooFewArgs );
        assertThat( cmd.hasOption( "p" ), is( true ) );
        assertThat( cmd.hasOption( "property" ), is( true ) );
        assertThat( cmd.getOptionValues( "p" ).length, is( 1 ) );

        cmd = parser.parse( options, passButNoOption );
        assertThat( cmd.hasOption( "p" ), is( false ) );
        assertThat( cmd.hasOption( "property" ), is( false ) );

        boolean missingArg = false;
        try
        {
            cmd = parser.parse( options, failNoArgs );
        }
        catch ( MissingArgumentException e )
        {
            missingArg = true;
        }
        assertThat( missingArg, is( true ) );

        boolean unrecognizedOperation = false;
        try
        {
            cmd = parser.parse( options, failIllegalArg );
        }
        catch ( UnrecognizedOptionException e )
        {
            unrecognizedOperation = true;
        }
        assertThat( unrecognizedOperation, is( true ) );
    }

    public void not_a_test() throws ParseException
    {
        Options options = null;
        int printedRowWidth = 100;
        String header = "";
        String footer = "";
        int spacesBeforeOption = 3;
        int spacesBeforeOptionDescription = 5;
        boolean displayUsage = true;
        printHelp( options, printedRowWidth, header, footer, spacesBeforeOption, spacesBeforeOptionDescription,
                displayUsage, System.out );

        String applicationName = "DataChecker";
        printUsage( applicationName, printedRowWidth, options, System.out );
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
        final String commandLineSyntax = "java -cp datachecker-0.1-SNAPSHOT.jar com.ldbc.datachecker.socialnet.SocialNetCheck";
        final PrintWriter writer = new PrintWriter( out );
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp( writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption,
                spacesBeforeOptionDescription, footer, displayUsage );
        writer.flush();
    }
}
