scriptbasedir=$(dirname "$0")
source $scriptbasedir/../properties.sh
curl -i $mulebaseurl/series/$1
