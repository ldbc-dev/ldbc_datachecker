package com.ldbc.datachecker.socialnet;

import java.io.File;

import com.ldbc.datachecker.Utils;

public class SocialNetConstants
{
    public static String[] allCsvFilenames( File directory )
    {
        // TODO get from file
        String[] filenames = new String[] { "comment.csv", "forum.csv", "organisation.csv", "person.csv", "place.csv",
                "post.csv", "tagclass.csv", "tag.csv", "comment_hasCreator_person.csv",
                "comment_isLocatedIn_place.csv", "comment_replyOf_comment.csv", "comment_replyOf_post.csv",
                "forum_containerOf_post.csv", "forum_hasMember_person.csv", "forum_hasModerator_person.csv",
                "forum_hasTag_tag.csv", "person_email_emailaddress.csv", "person_hasInterest_tag.csv",
                "person_isLocatedIn_place.csv", "person_knows_person.csv", "person_likes_post.csv",
                "person_speaks_language.csv", "person_studyAt_organisation.csv", "person_workAt_organisation.csv",
                "place_isPartOf_place.csv", "post_hasCreator_person.csv", "post_hasTag_tag.csv",
                "post_isLocatedIn_place.csv", "tagclass_isSubclassOf_tagclass.csv", "tag_hasType_tagclass.csv",
                "organisation_isLocatedIn_place.csv" };

        return Utils.prefixArrayElementsWith( filenames, directory.getAbsolutePath() + "/" );
    }

    public static String[] placeTypes()
    {
        // TODO get from file
        return new String[] { "city", "country", "continent" };
    }

    public static String[] organisationTypes()
    {
        // TODO get from file
        return new String[] { "university", "company" };
    }

    public static String[] genders()
    {
        // TODO get from file
        return new String[] { "male", "female" };
    }

    public static String[] browsers()
    {
        // TODO get from file
        return new String[] { "Firefox", "Chrome", "Internet Explorer", "Safari", "Opera" };
    }

    public static String[] languages( boolean optional )
    {
        return ( true == optional ) ? Utils.joinArrays( new String[] { "" }, languages() ) : languages();
    }

    public static String[] languages()
    {
        // TODO get from file
        return new String[] { "en", "zh", "ru", "hi", "kn", "te", "cc", "am", "ws", "us", "lv", "pt", "gu", "ja", "es",
                "sv", "ar", "it", "fr", "br", "ur", "uk", "ro", "vi", "tr", "ku", "or", "as", "ta", "pl", "de", "gl",
                "sd", "my", "ml", "az", "nl", "ha", "bn", "jv", "mr", "tl", "fa", "ny", "sk", "cs", "fo", "mg", "th",
                "be", "ne", "kk", "ko", "rw", "hu", "ay", "af", "sr", "hr", "ca", "tg", "tk", "he", "eu", "si", "wo",
                "uz", "mt", "qu", "pa", "mi", "to", "fi", "ln", "ga", "bg", "co", "lt", "da", "et", "lo", "sl", "mk",
                "cy", "bs", "hy", "sh", "sq", "ms", "hz" };
    }

    public static String locationIpRegex()
    {
        return "^\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}$";
    }

    public static String imageFileRegex( boolean optional )
    {
        String imagePattern = ".*\\.jpg$";
        return ( false == optional ) ? imagePattern : "|" + imagePattern;
    }

    public static String dateTimeFormat()
    {
        return "yyyy-MM-dd'T'HH:mm:ss'Z'";
    }

    public static String dateFormat()
    {
        return "yyyy-MM-dd";
    }
}
