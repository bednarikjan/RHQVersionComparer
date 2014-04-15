RHQVersionComparer
==================

performs comparison of 2 versions of RHQ

the following command do the comparison:

1. java -jar RHQVersionComparer.jar ${RHQ_HOME_DIR_V1}
will generate a hash of all files in old rhq home dirctory and store into txt file

2. java -jar RHQVersionComparer.jar ${RHQ_HOME_DIR_V2} ${CP_UPDATE_FOLDER}
will check whether files removed or modified during update process are backed up somewhere within folder ${CP_UPDATE_FOLDER}. This run uses the txt file generated in previous run. All files that are not backed up (and should be) are printed in console.


