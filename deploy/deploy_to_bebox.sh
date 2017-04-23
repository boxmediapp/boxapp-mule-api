source /Users/dilshathewzulla/box/box-secrets/bebox.sh
echo "deploying the version 3.0.5-SNAPSHOT to ec2-user@be.boxnetwork.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/bebox.sh (for replacement of the environment specific variables) ..."
deploy/deploy.sh be.boxnetwork.co.uk ec2-user 3.0.5-SNAPSHOT
