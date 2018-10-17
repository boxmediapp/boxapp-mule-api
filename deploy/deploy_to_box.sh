source /Users/dilshathewzulla/box/box-secrets/box.sh
echo "deploying the version 5.0.4-SNAPSHOT to ec2-user@mediapp.iterativesolution.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/box.sh (for replacement of the environment specific variables) ..."
deploy/deploy.sh mediapp.iterativesolution.co.uk ec2-user 5.0.4-SNAPSHOT
