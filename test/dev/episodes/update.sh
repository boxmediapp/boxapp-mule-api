scriptbasedir=$(dirname "$0")
source $scriptbasedir/../properties.sh
curl -i -X PUT -H "Content-Type:application/json" -d @$scriptbasedir/data/$1.json $mulebaseurl/episodes/$1
