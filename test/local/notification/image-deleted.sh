scriptbasedir=$(dirname "$0")
source $scriptbasedir/../properties.sh
curl -i -X POST -H "Content-Type:application/json" -d @$scriptbasedir/data/image-deleted.json $mulebaseurl/notify/s3

