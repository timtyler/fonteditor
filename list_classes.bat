cd source
jikes -classpath .;C:\j2sdk1.4.1_01\jre\lib\rt.jar +U -Xstdout -nowrite -verbose Run.java > ../file_list_raw.txt
cd ..
grep write file_list_raw.txt >file_list.txt
del file_list_raw.txt 
