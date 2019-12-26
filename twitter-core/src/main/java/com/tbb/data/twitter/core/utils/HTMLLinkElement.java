package com.tbb.data.twitter.core.utils;

/**
 * Created by rshaikh3145 on 11/12/2017.
 */
public class HTMLLinkElement {

    private String linkElement;
    private String linkAddress;

    public String getLinkAddress() {
        return linkAddress;
    }

    public void setLinkAddress(String linkElement) {
        this.linkAddress = replaceInvalidChar(linkElement);
    }

    public String getLinkElement() {
        return linkElement;
    }

    public void setLinkElement(String linkAddress) {
        this.linkElement = linkAddress;
    }

    private String replaceInvalidChar(String linkElement) {
        linkElement = linkElement.replaceAll("'", "");
        linkElement = linkElement.replaceAll("\"", "");
        return linkElement;
    }

    @Override
    public String toString() {
        return "Link Address : " + this.linkAddress + " \nLink Element : "
                + this.linkElement;
    }
}