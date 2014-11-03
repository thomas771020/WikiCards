package org.vosie.wikicards;

import java.io.File;
import java.io.IOException;

import org.vosie.wikicards.utils.WordSoundFileUtils;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

public class RecorderTest extends ActivityInstrumentationTestCase2<MainActivity> {

  public RecorderTest() {
    super(MainActivity.class);
  }

  public void testRecordSound() throws IllegalStateException, IOException {

    Activity act = getActivity();
    File f = WordSoundFileUtils.getFile(act, "testLang", "testServerID");

    Recorder recorder = new Recorder(f);

    recorder.start();
    recorder.stop();

    File recordsDir = act.getDir("user_records", Context.MODE_PRIVATE);
    String fileName = "snd_user_testLang" + "testServerID".hashCode() + ".3gp";
    f = new File(recordsDir, fileName);
    assertTrue(f.exists());
  }

}
