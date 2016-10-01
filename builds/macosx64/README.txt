Java Chromium Embedded Framework (JCEF) Binary Distribution for Mac OS-X 64-bit
-------------------------------------------------------------------------------

Date:             February 13, 2016

JCEF Version:     3.2526.136.ga5f1e20
JCEF URL:         https://bitbucket.org/chromiumembedded/java-cef.git
                  @a5f1e20ac811b5d2fe3066a815e84133ec1177de

CEF Version:      3.2526.1373.gb660893
CEF URL:          https://bitbucket.org/chromiumembedded/cef.git

Chromium Verison: 47.0.2526.80
Chromium URL:     https://chromium.googlesource.com/chromium/src.git

This distribution contains all components necessary to build and distribute a
Java application using JCEF on the Mac OS-X 64-bit platform. Please see the LICENSING
section of this document for licensing terms and conditions.


CONTENTS
--------

bin         Contains the jcef_app whose contents include Java archives and
            native library files.

docs        Contains documentation for the org.cef package.

tests       Contains the MainFrame sample application.

USAGE
-----

1. Install a 64-bit version of the Oracle Java 7 runtime.
2. Launch jcef_app to run the MainFrame sample application.
3. Optionally recompile the sample application and update jcef-tests.jar
   in the jcef_app bundle by running the compile.sh script.

Please visit the JCEF Website for additional usage information.

https://bitbucket.org/chromiumembedded/java-cef


REDISTRIBUTION
--------------

This binary distribution contains the below components. Components listed under
the "required" section must be redistributed with all applications using JCEF.
Components listed under the "optional" section may be excluded if the related
features will not be used.

Applications using JCEF on OS X must follow a specific app bundle structure.
Replace "jcef_app" in the below example with your application name.

jcef_app.app/
  Contents/
    Frameworks/
      Chromium Embedded Framework.framework/
        Chromium Embedded Framework <= main application library
        Resources/
          cef.pak <= non-localized resources and strings
          cef_100_percent.pak <====^
          cef_200_percent.pak <====^
          cef_extensions.pak <=====^
          devtools_resources.pak <=^
          crash_inspector, crash_report_sender <= breakpad support
          icudtl.dat <= unicode support
          natives_blob.bin, snapshot_blob.bin <= V8 initial snapshot
          en.lproj/, ... <= locale-specific resources and strings
          Info.plist
      jcef Helper.app/
        Contents/
          Info.plist
          MacOS/
            jcef Helper <= helper executable
          Pkginfo
      jcef Helper EH.app/
        Contents/
          Info.plist
          MacOS/
            jcef Helper EH <= helper executable
          Pkginfo
      jcef Helper NP.app/
        Contents/
          Info.plist
          MacOS/
            jcef Helper NP <= helper executable
          Pkginfo
      Info.plist
    Java/
      *.jar <= Required JAR files
      libjcef.dylib <= CEF JNI library
    MacOS/
      JavaAppLauncher <= Java bootstrap executable
    Pkginfo
    Resources/
      CefIcon.icns
      en.lproj/Localizable.strings

The "Chromium Embedded Framework.framework" is an unversioned framework that
contains CEF binaries and resources. Binaries (libjcef.dylib, jcef Helper,
etc) are linked to the "Chromium Embedded Framework" library using
install_name_tool and a path relative to @executable_path.

The "jcef Helper" apps are used for executing separate processes (renderer,
plugin, etc) with different characteristics. They need to have separate app
bundles and Info.plist files so that, among other things, they don't show dock
icons. The "EH" helper, which is used when launching plugin processes, has the
MH_NO_HEAP_EXECUTION bit cleared to allow an executable heap. The "NP" helper,
which is used when launching NaCl plugin processes only, has the MH_PIE bit
cleared to disable ASLR. This is set up as part of the build process using
scripts from the CEF binary distribution tools/ directory.

Required components:

The following components are required. CEF will not function without them.

* Java archives
    jcef.jar
    gluegen-rt.jar
    gluegen-rt-natives-macosx-universal.jar
    jogl-all.jar
    jogl-all-natives-macosx-universal.jar

* CEF JNI library
    libjcef.dylib

* CEF core library.
  * Chromium Embedded Framework.framework/Chromium Embedded Framework

* Unicode support data.
  * Chromium Embedded Framework.framework/Resources/icudtl.dat

* V8 snapshot data.
  * Chromium Embedded Framework.framework/Resources/natives_blob.bin
  * Chromium Embedded Framework.framework/Resources/snapshot_blob.bin

Optional components:

The following components are optional. If they are missing CEF will continue to
run but any related functionality may become broken or disabled.

* Localized resources.
  Locale file loading can be disabled completely using
  CefSettings.pack_loading_disabled.

  * Chromium Embedded Framework.framework/Resources/*.lproj/
    Directory containing localized resources used by CEF, Chromium and Blink. A
    .pak file is loaded from this directory based on the CefSettings.locale
    value. Only configured locales need to be distributed. If no locale is
    configured the default locale of "en" will be used. Without these files
    arbitrary Web components may display incorrectly.

* Other resources.
  Pack file loading can be disabled completely using
  CefSettings.pack_loading_disabled.

  * Chromium Embedded Framework.framework/Resources/cef.pak
  * Chromium Embedded Framework.framework/Resources/cef_100_percent.pak
  * Chromium Embedded Framework.framework/Resources/cef_200_percent.pak
    These files contain non-localized resources used by CEF, Chromium and Blink.
    Without these files arbitrary Web components may display incorrectly.

  * Chromium Embedded Framework.framework/Resources/cef_extensions.pak
    This file contains non-localized resources required for extension loading.
    Pass the `--disable-extensions` command-line flag to disable use of this
    file. Without this file components that depend on the extension system,
    such as the PDF viewer, will not function.

  * Chromium Embedded Framework.framework/Resources/devtools_resources.pak
    This file contains non-localized resources required for Chrome Developer
    Tools. Without this file Chrome Developer Tools will not function.

* Breakpad support.
  * Chromium Embedded Framework.framework/Resources/crash_inspector
  * Chromium Embedded Framework.framework/Resources/crash_report_sender
  * Chromium Embedded Framework.framework/Resources/Info.plist
    Without these files breakpad support (crash reporting) will not function.


LICENSING
---------

The JCEF project is BSD licensed. Please read the LICENSE.txt files included with
this binary distribution for licensing terms and conditions. Other software
included in this distribution is provided under other licenses. Please visit
"about:credits" in a JCEF-based application for complete Chromium and third-party
licensing information.
