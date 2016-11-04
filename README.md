# ACO-Project

To complile and run from this folder via command line

Compile: 
javac -cp "./src:./src/jcommon-1.0.23.jar:./src/jfreechart-1.0.19.jar" ./src/Main.java

Run: 
java -cp "./src:./src/jcommon-1.0.23.jar:./src/jfreechart-1.0.19.jar" Main

Note that included are two binary files, "jcommon-1.0.23.jar" and "jfeechart-1.0.19.jar", these are two libraries used to add in
chart generation. Both libraries use the [LGPL](http://www.gnu.org/licenses/lgpl.html) license, and allows for the inclusion of the
binaries, as long as the library can be updated independently. This can be done by simply downloading or building a binary and adding
it to the classpath for compilation. 
