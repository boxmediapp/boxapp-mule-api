scriptbasedir=$(dirname "$0")
source $scriptbasedir/../properties.sh
curl -H "Content-Type:application/json"  -d @$scriptbasedir/data/ingest.json -X POST $mulebaseurl/bc/ingest
