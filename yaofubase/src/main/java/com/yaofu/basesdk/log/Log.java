package com.yaofu.basesdk.log;


import com.yaofu.basesdk.BuildConfig;

public class Log  extends YLog {
  /*
   * set the LEVEL with VERBOSE value for inner debug version
   */
  public static final int LEVEL = android.util.Log.VERBOSE;


/*  public static void v(String msgFormat) {
    if (LEVEL <= android.util.Log.VERBOSE && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String fileName = ste.getFileName();
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.v(fileName, traceInfo + msgFormat);
    }
  }*/

 /* public static void v(String tag, String msgFormat) {

    if (LEVEL <= android.util.Log.VERBOSE && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.v(tag, traceInfo + msgFormat);
    }
  }*/

  public static void v(String tag, String msgFormat, Throwable t) {
    if (LEVEL <= android.util.Log.VERBOSE && BuildConfig.DEBUG) {
      android.util.Log.v(tag, msgFormat, t);
    }
  }

  // debug log
 /* public static void d(String msgFormat) {
    if (LEVEL <= android.util.Log.DEBUG && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String fileName = ste.getFileName();
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.d(fileName, traceInfo + msgFormat);
    }
  }

  public static void d(String tag, String msgFormat) {
    if (LEVEL <= android.util.Log.DEBUG && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.d(tag, traceInfo + msgFormat);
    }
  }*/

  public static void d(String tag, String msgFormat, Throwable t) {
    if (LEVEL <= android.util.Log.DEBUG && BuildConfig.DEBUG) {
      android.util.Log.d(tag, msgFormat, t);
    }
  }

  // info log
 /* public static void i(String msgFormat) {
    if (LEVEL <= android.util.Log.INFO && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String fileName = ste.getFileName();
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.i(fileName, traceInfo + msgFormat);
    }
  }

  public static void i(String tag, String msgFormat) {
    if (LEVEL <= android.util.Log.INFO && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.i(tag, traceInfo + msgFormat);
    }
  }*/

  public static void i(String tag, String msgFormat, Throwable t) {
    if (LEVEL <= android.util.Log.INFO && BuildConfig.DEBUG) {
      android.util.Log.i(tag, msgFormat, t);
    }
  }

  // Warning log
 /* public static void w(String msgFormat) {
    if (LEVEL <= android.util.Log.WARN && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String fileName = ste.getFileName();
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.w(fileName, traceInfo + msgFormat);
    }
  }

  public static void w(String tag, String msgFormat) {
    if (LEVEL <= android.util.Log.WARN && BuildConfig.DEBUG) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.w(tag, traceInfo + msgFormat);
    }
  }*/

  public static void w(String tag, String msgFormat, Throwable t) {
    if (LEVEL <= android.util.Log.WARN && BuildConfig.DEBUG) {
      android.util.Log.w(tag, msgFormat, t);
    }
  }

  // Error log
 /* public static void e(String msgFormat) {
    if (LEVEL <= android.util.Log.ERROR) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String fileName = ste.getFileName();
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.e(fileName, traceInfo + msgFormat);
    }
  }

  public static void e(String tag, String msgFormat) {
    if (LEVEL <= android.util.Log.ERROR) {
      StackTraceElement ste = new Throwable().getStackTrace()[1];
      String traceInfo = ste.getClassName() + "::";
      traceInfo += ste.getMethodName();
      traceInfo += "@" + ste.getLineNumber() + ">>>";

      android.util.Log.e(tag, traceInfo + msgFormat);
    }
  }*/

  public static void e(String tag, String msgFormat, Throwable t) {
    if (LEVEL <= android.util.Log.ERROR) {
      android.util.Log.e(tag, msgFormat, t);
    }
  }

  // verbose log
  public static void v(String tag, String msgFormat, Object... args) {

    if (LEVEL <= android.util.Log.VERBOSE && BuildConfig.DEBUG) {
      android.util.Log.v(tag, String.format(msgFormat, args));
    }
  }

  public static void v(String tag, Throwable t, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.VERBOSE && BuildConfig.DEBUG) {
      android.util.Log.v(tag, String.format(msgFormat, args), t);
    }
  }

  // debug log
 /* public static void d(String tag, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.DEBUG && BuildConfig.DEBUG) {
      android.util.Log.d(tag, String.format(msgFormat, args));
    }
  }*/

  public static void d(String tag, Throwable t, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.DEBUG && BuildConfig.DEBUG) {
      android.util.Log.d(tag, String.format(msgFormat, args), t);
    }
  }

  // info log
/*  public static void i(String tag, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.INFO && BuildConfig.DEBUG) {
      android.util.Log.i(tag, String.format(msgFormat, args));
    }
  }*/

  public static void i(String tag, Throwable t, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.INFO && BuildConfig.DEBUG) {
      android.util.Log.i(tag, String.format(msgFormat, args), t);
    }
  }

  // Warning log
/*  public static void w(String tag, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.WARN && BuildConfig.DEBUG) {
      android.util.Log.w(tag, String.format(msgFormat, args));
    }
  }*/

  public static void w(String tag, Throwable t, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.WARN && BuildConfig.DEBUG) {
      android.util.Log.w(tag, String.format(msgFormat, args), t);
    }
  }

  // Error log
/*
  public static void e(String tag, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.ERROR) {
      android.util.Log.e(tag, String.format(msgFormat, args));
    }
  }
*/

  public static void e(String tag, Throwable t, String msgFormat, Object... args) {
    if (LEVEL <= android.util.Log.ERROR) {
      android.util.Log.e(tag, String.format(msgFormat, args), t);
    }
  }
}
