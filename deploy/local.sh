source deploy/util.sh


buildLocalVariables(){
  export appzipfilenamebase="boxtv-metadata-app-"
  export appzipfilename="$appzipfilenamebase$projectversion.zip"
  
  export appsourcezipfilepath="target/$appzipfilename"  
  export appdestfolder="~/bdocker/bmule/opt/mule/apps"
  
  export configzipfilename="box-config-$projectversion.zip"
  export configsourcezipfilepath="box-config/target/$configzipfilename"  
  export configdestfolder="/Users/dilshathewzulla/bdocker/bmule/opt/mule/"     
}

deployLocalConfig(){
    echo "deploying the configuration: $configsourcezipfilepath $configdestfolder"    
    cp $configsourcezipfilepath $configdestfolder
    createUniqueidforfilename    
    
    unzipConfigAndReplaceVariables $uniqueidforfilename
    chmod u+x /tmp/script_$uniqueidforfilename.sh
    /tmp/script_$uniqueidforfilename.sh        
}





deploy_to_hostname=""
deploy_to_username=""
projectversion=$3

buildLocalVariables

deployLocalConfig


