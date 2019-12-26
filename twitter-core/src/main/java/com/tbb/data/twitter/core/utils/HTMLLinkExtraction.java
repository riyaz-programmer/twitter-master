package com.tbb.data.twitter.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rshaikh3145 on 11/12/2017.
 */
public class HTMLLinkExtraction {

    private static Matcher mTag, mLink;


    private static final String HTML_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
    private static final String HTML_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
    private static final Pattern pTag = Pattern.compile(HTML_TAG_PATTERN);
    private static final Pattern pLink = Pattern.compile(HTML_HREF_TAG_PATTERN);

    public static synchronized HTMLLinkElement extractHTMLLinks(final String sourceHtml) {

        HTMLLinkElement htmlLinkElement = new HTMLLinkElement();
        mTag = pTag.matcher(sourceHtml);

        while (mTag.find()) {

            String href = mTag.group(1);     // get the values of href
            String linkElem = mTag.group(2); // get the text of link Html Element
            mLink = pLink.matcher(href);
            if (!linkElem.equals("Twitter Web Client")) {
                String[] appName = linkElem.split(" ");
                linkElem = (appName[appName.length - 1]).trim();
            } else {
                linkElem="Twitter";
            }
            while (mLink.find()) {
                String link = mLink.group(1);
                htmlLinkElement.setLinkAddress(link);
                htmlLinkElement.setLinkElement(linkElem);
            }
        }
        return htmlLinkElement;
    }
}