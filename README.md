# smudgr

smudgr is a unique visuals app for designing, producing, and performing algorithmic manipulations.

+ Visual instruments, called "smudges", are sequenced and layered.
+ Smudges are musically reactive, tightly controlled, or beautifully expressive.
+ Process and expression compliment and contrast each other.

# Build/Launch

Requires JRE/JDK 1.8. Eclipse or IntelliJ recommended.

Requires [Node.js/NPM] (https://nodejs.org/en/). Make sure `npm` is on your path.

Requires [Ant] (https://ant.apache.org/manual/install.html), which may be included in your Java IDE, but you can use `brew install ant` on OSX, or install manually on Windows.

`ant build`

This should install dependencies, compile code, and bundle an executable in builds/your_os

Alternatively,

`ant launch` will build and launch the executable for your platform.

# Headless

You can run a test class without launching the front-end using:

`ant run -Dclass=io.smudgr.PizzaBoxShow`
