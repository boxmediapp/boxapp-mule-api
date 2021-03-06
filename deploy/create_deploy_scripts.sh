source deploy/util.sh

createDeployScript dubuntu  $projectversion  ~/box/box-secrets/box.sh  dubuntu              dilshat
createDeployScript dev      $projectversion  ~/box/box-secrets/box.sh  userver              davran
createDeployScript box      $projectversion  ~/box/box-secrets/box.sh  mediapp.iterativesolution.co.uk     ec2-user
createDeployScript bebox    $projectversion  ~/box/box-secrets/bebox.sh  bemediaapp.iterativesolution.co.uk  ec2-user
createDeployScript image    $projectversion  ~/box/box-secrets/image.sh  image.boxnetwork.co.uk  ec2-user

createLocalDeployScript local    $projectversion  ~/box/box-secrets/local.sh     image.boxnetwork.co.uk  ec2-user
createLocalDeployScript readonly $projectversion  ~/box/box-secrets/readonly.sh  image.boxnetwork.co.uk  ec2-user

createLocalDeployScript localprod $projectversion  ~/box/box-secrets/localprod.sh  image.boxnetwork.co.uk  ec2-user



