package org.vosie.wikicards.test.mock.utils;

import java.io.File;

import org.vosie.wikicards.Recorder;
import org.vosie.wikicards.utils.RecordUtils;

public class MockReordUtils extends RecordUtils {

  public static MockReordUtils createInstance() {
    MockReordUtils ret = new MockReordUtils();
    synchronized (MockReordUtils.class) {
      RecordUtils.instance = ret;
    }
    return ret;
  }

  public Recorder createdRecorder;

  @Override
  public Recorder createRecorder(File f) {
    createdRecorder = super.createRecorder(f);
    return createdRecorder;
  }
}
