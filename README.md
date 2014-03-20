RHQVersionComparer
==================

performs comparison of 2 versions of RHQ

the following command do the comparison:

1. java -jar RHQVersionComparer.jar ${RHQ_HOME_DIR_V1}
will generate a hash of all files in old rhq home dirctory and store into txt file

2. java -jar RHQVersionComparer.jar ${RHQ_HOME_DIR_V2} compare
will compare files from new rhq home directory to the hash data from txt file generated at step 1

Changed, removed, added files will be printed out for *manual* comparison


