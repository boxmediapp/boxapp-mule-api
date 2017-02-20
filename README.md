# bdocker

This project a Mule ESB component, which can be deployed to the docker container created by   


           https://github.com/boxmediapp/bdocker
   
 The HTML5 client application component consuming the services implement by this application is provided at:
 
          https://github.com/boxmediapp/box-media-client-app
 
   
 The steps for building and deploying the MULE ESB application:
  
                
(1) Checkout the git repository:
 
          git clone   https://github.com/boxmediapp/boxapp-mule-api.git

(2) create the script to set the environmental specific variables such as AWS access key and secrets etc, you can copy and modify "deploy/prod.sh" for this. Since the script file that you have created contains the sensitive information, you can put it outside the project folder.

and then you need to modify the script "deploy/create_deploy_scripts.sh" to add a new line

     createDeployScript <serverid>    $projectversion  <path-to-the-prod.sh-that-sets-variables>  <your-target-server-host-name>  <the-script-settings-the-environment-specific-variables>

where <serverid> can be any word that can uniquey identify your target server, so that you can identify the generated deployment script in the form of:

                deploy/deploy_to_<server-id>.sh
                
   
(3) Run a terminal and go into the project folder:
 
         cd box-mule-api

(3) and then run the script to create the zip file
 
         build/package.sh

this will create a deployment zip file and  a configuration zip file. 

It also generates the deployment script in the form of "deploy/deploy_to_<server-id>.sh"

       
(4)Execute generated deployment script:

         build/deploy_to_<server-id>.sh
         
   this will deploy the application to the target server.
   
   
   
### configuration folder

In the step 2, we have talked about you need to create a script (deploy/prod.sh as the reference) to specify the environmental specific variables.
  
you may wonder where the environmental specific variables may go. 

The variables are used to replace the place holder variables, marked with a pair of "@@@" in the files located in "box-config" folder.
       
The description about the folders and the files in the configuration folder:

+ **aws**
     - *config*: AWS config file, it contains the AWS region etc information where the S3 bucket is located
     - *credentials*: the AWS credentials for accessing the s3 bucket. The place holders marked with a pair of "@@@" should be replaced with actual value either manually or at the deployment by the deployment script
    
+ **metadata-config**
     -  *app-config.properties*:  application configurations that will be stored in the database and configurable via the client web application.
     -  *db.properties*: the database connection information such as user name and password.
     -  *http.properties*: the http paths that the application is going to bind its services to 
     -  *services.properties*: the configuration used by the application to connect to the back-end services. For example brightcove end points and credentials so that the application can publish the media to the brightcove. The place holders marked with a pair of "@@@" should be replaced with actual value either manually or at the deployment by the deployment script
 
