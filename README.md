# bdocker

This is a Mule ESB project, which can be deployed to the docker container created by   

           https://github.com/boxmediapp/bdocker
   
 The HTML5 client application component consuming the services implement by this application is provided at:
 
          https://github.com/boxmediapp/box-media-client-app
 
   
 The steps for building and deploying the MULE ESB application:
  
                
(1) Checkout the git repository:
 
          git clone   https://github.com/boxmediapp/boxapp-mule-api.git
          
(2) create a configuration script  <environment-id>.sh, which specifies the environt specific variables such the database authentication etc. 
    The name of the script can be dev.sh, int.sh, test.sh, stage.sh and prod.sh and its content can be:

           export db_user=<any-user-name-you-like-for-database-access>
           export db_password=<password-you-like-to-be-set-for-database-access>
           ....                     
It is recommended that you can created a separate git project to store this type of information, the base name of the script is the unique name of the environment that will be used later in the next step.


(3) modify the script "deploy/create_deploy_scripts.sh" to add a new line to the corresponding script that you have created in the previous step. 
This is for loading the environmental specific configuration. The content you are going to add can be like:

     createDeployScript <serverid>     $projectversion  <path-to-the-script-that-sets-variables>  <your-target-server-host-name>  <username-connecting-to-target-server>
     

where <serverid> is the unique id of the environment you are going to deploy to, and it is the same in the previous step. 

As the result, 

When the project is build, the corresponding script "deploy/deploy_to_<server-id>.sh" wil be created for you to deploy the files to your environment.
        
        
          
            
   
(4) Run a terminal and go into the project folder:
 
         cd box-mule-api

(5) and then run the script to create the zip file
 
         build/package.sh

this will create a deployment zip file and  a configuration zip file. 

It also generates the deployment script in the form of "deploy/deploy_to_<server-id>.sh"

       
(6)Execute generated deployment script:

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
 
