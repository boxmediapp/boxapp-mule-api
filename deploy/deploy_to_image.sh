source /Users/dilshathewzulla/box/box-secrets/image.sh
echo "deploying the version 4.0.8 to ec2-user@image.boxnetwork.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/image.sh (for replacement of the environment specific variables) ..."
deploy/deploy.sh image.boxnetwork.co.uk ec2-user 4.0.8
