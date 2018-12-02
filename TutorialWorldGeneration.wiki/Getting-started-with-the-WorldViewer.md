Now that you have created a world generator, you could also use the WorldViewer to check your work and see how your world looks as a 2d representation.

##Getting Started

First off you need to build jar of your world generator.  Do so by with gradle like this: ```gradlew jar``` at a command line.  This will build you module's code into a jar.  For the TutorialWorldGenerator, the jar is located at: ```<Terasology Root>\modules\TutorialWorldGeneration\build\libs\TutorialWorldGeneration-0.1.0-SNAPSHOT.jar```.  Remember this path for later.

Next,  go get the [WorldViewer](https://github.com/MovingBlocks/WorldViewer) from github here: http://jenkins.terasology.org/job/WorldViewer/lastSuccessfulBuild/artifact/build/distributions/WorldViewer.zip

Then, we must insert this jar's path into the classpath of the WorldViewer.  Do this by editing the ```<WorldViewer>\bin\WorldViewer.bat``` so that the module's jar is tacked on to the end of the big wall of classpath.  On my machine,  I ended up typing this: ```;C:\Projects\Terasology\modules\TutorialWorldGeneration\build\libs\TutorialWorldGeneration-0.1.0-SNAPSHOT.jar```

Finally,  run the WorldViewer using the batch file we just edited.  Select the world generator from the dialog box and win the day!