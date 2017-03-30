package com.halfhp.lex;

import org.junit.Ignore;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;
import org.robolectric.res.ResourcePath;

import java.util.List;

/**
 * Enables using resources in src/test/res in unit tests:
 * https://stackoverflow.com/questions/27618660/using-multiple-res-folders-with-robolectric
 */
@Ignore
public class LexTestRunner extends RobolectricTestRunner {

    public LexTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String appRoot = "lex/src/main/";
        String manifestPath = appRoot + "AndroidManifest.xml";
        String resDir = appRoot + "res";
        String assetsDir = appRoot + "assets";

        //return new AndroidManifest(Fs.fileFromPath("src/main/AndroidManifest.xml"), Fs.fileFromPath(resDir), Fs.fileFromPath(assetsDir)) {
        return new AndroidManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath(resDir), Fs.fileFromPath(assetsDir)) {

            @Override
            public List<ResourcePath> getIncludedResourcePaths() {
                List<ResourcePath> paths = super.getIncludedResourcePaths();
                paths.add(new ResourcePath(getRClass(), Fs.fileFromPath("lex/src/main/res"), getAssetsDirectory()));
                paths.add(new ResourcePath(getRClass(), Fs.fileFromPath("lex/src/test/res"), getAssetsDirectory()));
                return paths;
            }
        };
    }
}
