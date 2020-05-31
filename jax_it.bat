cd bin

C:\Program1\Java\JDKs\IBM\bin\java -cp .;C:\Program1\Java\JDKs\IBM\jre\lib\rt.jar;C:\Incoming\Java\IBM\JAX\jax73.zip;C:\Documents\Java\FontEditor\bin -DJAVAHOME=C:\Program1\Java\JDKs\IBM com.ibm.jax.Batch FE.jax

cd ..

copy bin\FE.zip jax\FE.zip
del /Q bin\FE.zip

del /Q jax\*.class
del /Q jax\*.cff

copy source\org\fonteditor\fonts\*.* jax\org\fonteditor\fonts\*.*

copy source\org\fonteditor\textures\*.* jax\org\fonteditor\textures\*.*

cd jax

C:\Program1\Java\JDKs\IBM\bin\jar xf FE.zip
C:\Program1\Java\JDKs\IBM\bin\jar cmf Manifest.mf fe.jar *.class org

copy fe.jar ..\plugin\fe.jar
cd ..
