package org.vosie.wikicards.utils;

import java.io.File;

import android.content.Context;

public class WordSoundFileUtils {
  public static File getFile(Context cxt, String lang, String serverID) {
    File recordsDir = cxt.getDir("user_records", Context.MODE_PRIVATE);
    String fileName = "snd_user_" + lang + serverID.hashCode() + ".3gp";
    return new File(recordsDir, fileName);
  }
}
