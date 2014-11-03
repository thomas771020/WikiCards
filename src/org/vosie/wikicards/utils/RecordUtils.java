package org.vosie.wikicards.utils;

import java.io.File;

import org.vosie.wikicards.Recorder;

public class RecordUtils {

  protected static RecordUtils instance;

  public static RecordUtils get() {
    synchronized (RecordUtils.class) {
      if (null == instance) {
        instance = new RecordUtils();
      }
    }
    return instance;
  }

  public Recorder createRecorder(File f) {
    return new Recorder(f);
  }

}
