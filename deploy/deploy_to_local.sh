source /Users/dilshathewzulla/box/box-secrets/local.sh
echo "deploying the version 4.1.2 to ec2-user@image.boxnetwork.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/local.sh (for replacement of the environment specific variables) ..."
deploy/local.sh image.boxnetwork.co.uk ec2-user 4.1.2
