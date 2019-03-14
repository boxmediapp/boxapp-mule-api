source /Users/dilshathewzulla/box/box-secrets/image.sh
echo "deploying the version 5.1.4-SNAPSHOT to ec2-user@image.boxnetwork.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/image.sh (for replacement of the environment specific variables) ..."
deploy/deploy.sh image.boxnetwork.co.uk ec2-user 5.1.4-SNAPSHOT
