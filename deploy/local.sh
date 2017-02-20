source deploy/util.sh

source ~/box/box-secrets/dev.sh


getProjectVersionFromPom

buildVariables

mkdir -p ~/$appdestfolder
mkdir -p ~/$configdestfolder


cp $appsourcezipfilepath ~/$appdestfolder/
cp $configsourcezipfilepath ~/$configdestfolder

cd ~


createUniqueidforfilename    
   
unzipConfigAndReplaceVariables $uniqueidforfilename

chmod u+x /tmp/script_$uniqueidforfilename.sh
/tmp/script_$uniqueidforfilename.sh

    
    

