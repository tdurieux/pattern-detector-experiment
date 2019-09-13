package com.tagtraum.perf.gcviewer.view.model;

import static org.hamcrest.Matchers.is;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.tagtraum.perf.gcviewer.model.GCResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class {@link RecentGCResourcesModel}.
 */
public class TestRecentGCResourcesModel {
    /**
     * "Encoding" a fully qualified path as an url is handled differently on different platforms -> compensate for differences.
     */
    private String getPathPrefix() {
        String resourceNameAsUrlString = new GCResource("temp").getResourceNameAsUrlString();
        System.out.println("nameAsUrlString: " + resourceNameAsUrlString);
        String prefix = resourceNameAsUrlString.substring("file:/".length(), resourceNameAsUrlString.indexOf("temp"));
        System.out.println("prefix: " + prefix);
        return prefix;
    }

    @Test
    public void addString() throws MalformedURLException {
        String rootPath = getPathPrefix();
        RecentGCResourcesModel model = new RecentGCResourcesModel();

        String path = "temp/test.log";
        String filePath = "file:/" + rootPath + "temp/test.log";
        System.out.println("path=" + path);
        System.out.println("filePath=" + filePath);
        System.out.printf("path->file: %s%n", new File(path).toString());
        System.out.printf("filepath->url: %s%n", new URL(filePath).toString());
        System.out.printf("filepath->file: %s%n", new File(filePath).toString());
        System.out.printf("filepath->url(file): %s%n", new URL(new File(filePath).toString()));
        System.out.printf("path->file->url: %s%n", new File(path).toURI().toURL().toString());
        System.out.printf("path->url(file): %s%n", new URL("file:/" + new File(path).toString()));
        System.out.printf("filepath->file->url: %s%n", new File(filePath).toURI().toURL().toString());

        model.add(rootPath + "temp/test.log");
        Assert.assertThat("add first entry", model.getResourceNameGroups().size(), is(1));

        model.add(rootPath + "temp/test.log");
        Assert.assertThat("add identical entry", model.getResourceNameGroups().size(), is(1));

        model.add("file:/" + rootPath + "temp/test.log");
        System.out.println(model.toString());
        Assert.assertThat("add url entry of same file", model.getResourceNameGroups().size(), is(1));
    }

    @Test
    public void addList() {
        RecentGCResourcesModel model = new RecentGCResourcesModel();
        model.add(Arrays.asList(new GCResource("temp/test.log")));
        Assert.assertThat("add first entry", model.getResourceNameGroups().size(), is(1));

        model.add(Arrays.asList(new GCResource("temp/test.log")));
        Assert.assertThat("add identical entry", model.getResourceNameGroups().size(), is(1));

        model.add(Arrays.asList(new GCResource("file:/" + getPathPrefix() + "temp/test.log")));
        Assert.assertThat("add url entry of same file", model.getResourceNameGroups().size(), is(1));
    }

}
