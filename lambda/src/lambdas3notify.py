from __future__ import print_function

import json
import urllib
import boto3
import requests


print('Loading function')

s3 = boto3.client('s3')


def lambda_handler(event, context):
    #print("Received event: " + json.dumps(event, indent=2))

    # Get the object from the event and show its content type
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = urllib.unquote_plus(event['Records'][0]['s3']['object']['key']).decode('utf8')
    try:
        s3notification={"bucket":bucket,"key":key}
        resp = requests.post('http://dev.boxnetwork.co.uk/mule/boxtv/notify/s3', json=s3notification)        
        return resp
    except Exception as e:
        print(e)
        print('Error getting object {} from bucket {}. Make sure they exist and your bucket is in the same region as this function.'.format(key, bucket))

