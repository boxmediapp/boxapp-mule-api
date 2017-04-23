getProjectVersionFromPom(){
  projectversion=`grep -A 0 -B 2 "<packaging>" pom.xml  | grep version  | cut -d\> -f 2 | cut -d\< -f 1`
  export projectversion  
}

buildVariables(){
  export appzipfilenamebase="boxtv-metadata-app-"
  export appzipfilename="$appzipfilenamebase$projectversion.zip"
  
  export appsourcezipfilepath="target/$appzipfilename"  
  export appdestfolder="bdocker/bmule/opt/mule/apps"
  
  export configzipfilename="box-config-$projectversion.zip"
  export configsourcezipfilepath="box-config/target/$configzipfilename"  
  export configdestfolder="bdocker/bmule/opt/mule/"
     
}

  
executeScript(){
   echo "executing the script $1 remotely  on  $deploy_to_username@$deploy_to_hostname "
   ssh $deploy_to_username@$deploy_to_hostname 'bash -s' < $1      
   echo "remote execution completed"   
}
  
createFolders(){
    createUniqueidforfilename
    echo "creating the script for creating folder: /tmp/script_$uniqueidforfilename.sh"   
    echo "mkdir -p $appdestfolder" > /tmp/script_$uniqueidforfilename.sh
    echo "mkdir -p $configdestfolder" >> /tmp/script_$uniqueidforfilename.sh    
    executeScript /tmp/script_$uniqueidforfilename.sh
}
deployConfig(){
    echo "deploying the configuration:scp $configsourcezipfilepath $deploy_to_username@$deploy_to_hostname:$configdestfolder/"    
    scp $configsourcezipfilepath $deploy_to_username@$deploy_to_hostname:$configdestfolder/    
    createUniqueidforfilename    
    
    unzipConfigAndReplaceVariables $uniqueidforfilename
    
    executeScript /tmp/script_$uniqueidforfilename.sh        
}

unzipConfigAndReplaceVariables(){      
      uniqueidforfilename=$1      
      echo "creating the config install script:/tmp/script_$uniqueidforfilename.sh"      
      echo "cd $configdestfolder" > /tmp/script_$uniqueidforfilename.sh
      echo "unzip -o $configzipfilename" >> /tmp/script_$uniqueidforfilename.sh    
      echo "cd box-config" >> /tmp/script_$uniqueidforfilename.sh      
      echo  'sed -i -e "s,@@@aws_access_key_id@@@,'$box_aws_access_key_id',g" aws/credentials  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@aws_secret_access_key@@@,'$box_aws_secret_access_key',g" aws/credentials ' >> /tmp/script_$uniqueidforfilename.sh
      
      echo  'sed -i -e "s,@@@brightcove_account_id@@@,'$brightcove_account_id',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@brightcove_client_id@@@,'$brightcove_client_id',g" metadata-config/services.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@brightcove_client_secret@@@,'$brightcove_client_secret',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh
      
      
      echo  'sed -i -e "s,@@@brightcove_custom_fields_type@@@,'$brightcove_custom_fields_type',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh
      
      echo  'sed -i -e "s,@@@c4_schedule_get_user@@@,'$c4_schedule_get_user',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh 
      echo  'sed -i -e "s,@@@c4_schedule_get_password@@@,'$c4_schedule_get_password',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@c4_certification_get_user@@@,'$c4_certification_get_user',g" metadata-config/services.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@c4_certification_get_password@@@,'$c4_certification_get_password',g" metadata-config/services.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@boxtv_metadata_app_security_encryption@@@,'$boxtv_metadata_app_security_encryption',g" metadata-config/services.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@boxtv_metadata_app_security_defaultRootPassword@@@,'$boxtv_metadata_app_security_defaultRootPassword',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh

      echo  'sed -i -e "s,@@@soundmouse_ftp_host@@@,'$soundmouse_ftp_host',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@soundmouse_ftp_user@@@,'$soundmouse_ftp_user',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@soundmouse_ftp_password@@@,'$soundmouse_ftp_password',g" metadata-config/services.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@soundmouse_ftp_path@@@,'$soundmouse_ftp_path',g" metadata-config/services.properties ' >> /tmp/script_$uniqueidforfilename.sh



      echo  'sed -i -e "s,@@@db_user@@@,'$db_user',g" metadata-config/db.properties ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@db_password@@@,'$db_password',g" metadata-config/db.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      
      
      echo  'sed -i -e "s,@@@s3_video_bucket@@@,'$s3_video_bucket',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@s3_ingest_url@@@,'$s3_ingest_url',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh

	  echo  'sed -i -e "s,@@@s3_images_bucket@@@,'$s3_images_bucket',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
	  echo  'sed -i -e "s,@@@s3_images_url@@@,'$s3_images_url',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh

      echo  'sed -i -e "s,@@@box_app_config_version@@@,'$box_app_config_version',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@auto_publish_changes_to_brightcove@@@,'$auto_publish_changes_to_brightcove',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@box_app_visibility_category@@@,'$box_app_visibility_category',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      
      echo  'sed -i -e "s,@@@box_autoset_geo_allowed_countries@@@,'$box_autoset_geo_allowed_countries',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@box_auto_set_content_type@@@,'$box_auto_set_content_type',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      
      echo  'sed -i -e "s,@@@box_auto_set_tx_channel@@@,'$box_auto_set_tx_channel',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@box_auto_set_published_status@@@,'$box_auto_set_published_status',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      
      echo  'sed -i -e "s,@@@box_auto_create_place_holder@@@,'$box_auto_create_place_holder',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@box_publish_programme_info@@@,'$box_publish_programme_info',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
      echo  'sed -i -e "s,@@@box_auto_start_transcode@@@,'$box_auto_start_transcode',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
	
	   echo  'sed -i -e "s,@@@box_image_template_url@@@,'$box_image_template_url',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
	   echo  'sed -i -e "s,@@@box_convert_image_status@@@,'$box_convert_image_status',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh
	   echo  'sed -i -e "s,@@@box_send_update_to_soundmouse@@@,'$box_send_update_to_soundmouse',g" metadata-config/app-config.properties  ' >> /tmp/script_$uniqueidforfilename.sh

}


deployApp(){
     echo "uninstalling the previous version of the app...."
     ssh $deploy_to_username@$deploy_to_hostname "rm $appdestfolder/$appzipfilenamebase*.zip"
     ssh $deploy_to_username@$deploy_to_hostname "rm $appdestfolder/$appzipfilenamebase*.txt"
     echo "deploying the app:scp $appsourcezipfilepath $deploy_to_username@$deploy_to_hostname:$appdestfolder/"     
     scp $appsourcezipfilepath $deploy_to_username@$deploy_to_hostname:$appdestfolder/
        
}
packageConfig(){	
	cd box-config
    mvn versions:set -DnewVersion=$projectversion
    mvn package
  	cd ..
}
package(){
	mvn package
	packageConfig	
}
createDeployScript(){
    echo "source $3" > deploy/deploy_to_$1.sh
    echo 'echo "deploying the version '$2' to '$5'@'$4' using the property file '$3' (for replacement of the environment specific variables) ..."' >>  deploy/deploy_to_$1.sh
    echo "deploy/deploy.sh $4 $5 $2" >> deploy/deploy_to_$1.sh
    chmod u+x deploy/deploy_to_$1.sh
}
createUniqueidforfilename(){
  if [ -z "${uniqueidforfilename+x}" ] 
  then 
        uniqueidforfilename=$(date +%s)
     
 else
        export uniqueidforfilename=$((uniqueidforfilename+1))
 fi
}


initdatabase(){
    createUniqueidforfilename
    echo "creating the script for initdb: /tmp/script_$uniqueidforfilename.sh"   
    echo "docker exec mysql bash /box-scripts/initdb.sh" > /tmp/script_$uniqueidforfilename.sh        
    executeScript /tmp/script_$uniqueidforfilename.sh   
}
  