package com.bikiegang.ridesharing.pojo;

import java.util.HashSet;

/**
 * Created by hpduy17 on 8/13/15.
 */
public class PopularLocation extends LatLng implements PojoBase {
    private long id;
    private String name;
    private String address;
    private String backgroundImageLink = "";
    private HashSet<String> searcher = new HashSet<>();

    public PopularLocation() {
    }

    public PopularLocation(LatLng that, long id, String name, String address, HashSet<String> searcher,String backgroundImageLink) {
        super(that);
        this.id = id;
        this.name = name;
        this.address = address;
        this.searcher = searcher;
        this.backgroundImageLink = backgroundImageLink;
    }

    public PopularLocation(PopularLocation that) {
        super(that);
        this.id = that.id;
        this.name = that.name;
        this.address = that.address;
        this.searcher = that.searcher;
        this.backgroundImageLink = that.backgroundImageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HashSet<String> getSearcher() {
        return searcher;
    }

    public void setSearcher(HashSet<String> searcher) {
        this.searcher = searcher;
    }

    public String getBackgroundImageLink() {
        return backgroundImageLink;
    }

    public void setBackgroundImageLink(String backgroundImageLink) {
        this.backgroundImageLink = backgroundImageLink;
    }
}
