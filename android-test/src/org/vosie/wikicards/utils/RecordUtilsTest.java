package org.vosie.wikicards.utils;

import java.io.File;

import org.vosie.wikicards.MainActivity;
import org.vosie.wikicards.Recorder;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

public class RecordUtilsTest extends ActivityInstrumentationTestCase2<MainActivity> {

  public RecordUtilsTest() {
    super(MainActivity.class);
  }

  public void testCreateRecorder() {
    Activity act = getActivity();
    File recordsDir = act.getDir("user_records", Context.MODE_PRIVATE);
    String fileName = "snd_user_testLang" + "testServerID".hashCode() + ".3gp";
    File recordFile = new File(recordsDir, fileName);
    Recorder recorder = RecordUtils.get().createRecorder(recordFile);
    assertNotNull(recorder);
  }

}
