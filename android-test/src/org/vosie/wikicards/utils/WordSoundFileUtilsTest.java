package org.vosie.wikicards.utils;

import java.io.File;

import org.vosie.wikicards.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class WordSoundFileUtilsTest extends ActivityInstrumentationTestCase2<MainActivity> {

  public WordSoundFileUtilsTest() {
    super(MainActivity.class);
  }

  public void testGetFile() {
    File f = WordSoundFileUtils.getFile(getActivity(), "testLang", "testServerID");
    assertNotNull(f);
  }

}
