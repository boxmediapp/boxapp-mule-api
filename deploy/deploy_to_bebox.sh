source /Users/dilshathewzulla/box/box-secrets/bebox.sh
echo "deploying the version 3.1.7-SNAPSHOT to ec2-user@bemediaapp.iterativesolution.co.uk using the property file /Users/dilshathewzulla/box/box-secrets/bebox.sh (for replacement of the environment specific variables) ..."
deploy/deploy.sh bemediaapp.iterativesolution.co.uk ec2-user 3.1.7-SNAPSHOT
