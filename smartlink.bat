cd bin

C:\IBM\wsdd\wsdd4.0\ive\bin\jxelink.exe -cp .;C:\Program1\Java\JDKs\IBM\jre\lib\rt.jar;C:\Documents\Java\FontEditor\bin -follow -outputType jar -compressJar -removeUnused -removeUnused -makeAbstract -makeFinal -xta -optimize -refTree -startupClass Run fe.jar

rem copy fe.jar ..\plugin\fe.jar

cd ..

rem -checkRefs
