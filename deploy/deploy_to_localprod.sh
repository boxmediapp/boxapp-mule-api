source /Users/dilshathewzulla/box/box-secrets/localprod.sh
echo "deploying the version 5.1.4 to ec2-user@image.boxnetwork.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/localprod.sh (for replacement of the environment specific variables) ..."
deploy/local.sh image.boxnetwork.co.uk ec2-user 5.1.4
