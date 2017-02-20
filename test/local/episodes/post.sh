scriptbasedir=$(dirname "$0")
source $scriptbasedir/../properties.sh
curl -i  --user "dilshat:$2"  -X POST -H "Content-Type:application/json" -d @$scriptbasedir/data/$1.json $mulebaseurl/episodes
