source deploy/util.sh

createDeployScript dubuntu  $projectversion  ~/box/box-secrets/box.sh  dubuntu              dilshat
createDeployScript dev      $projectversion  ~/box/box-secrets/box.sh  userver              davran
createDeployScript box      $projectversion  ~/box/box-secrets/box.sh  boxnetwork.co.uk     ec2-user
createDeployScript bebox    $projectversion  ~/box/box-secrets/bebox.sh  be.boxnetwork.co.uk  ec2-user



