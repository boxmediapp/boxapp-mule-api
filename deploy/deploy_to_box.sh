source /Users/dilshathewzulla/box/box-secrets/box.sh
echo "deploying the version 3.0.9 to ec2-user@boxnetwork.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/box.sh (for replacement of the environment specific variables) ..."
deploy/deploy.sh mediapp.iterativesolution.co.uk ec2-user 3.0.9
