scriptbasedir=$(dirname "$0")
cat $scriptbasedir/../properties.sh
source $scriptbasedir/../properties.sh
curl -i -X POST -H "Content-Type:application/json" -d @$scriptbasedir/data/image-created-3.json $mulebaseurl/notify/s3

