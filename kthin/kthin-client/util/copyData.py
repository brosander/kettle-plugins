#!/usr/bin/python
import urllib2, base64
import shutil
import json
import json
import argparse
import os

if __name__ == '__main__':
  parser = argparse.ArgumentParser(description='''
  This script will copy the step/jobentry images from a running carte (with kthin-server) to the img folder
  ''', formatter_class = argparse.ArgumentDefaultsHelpFormatter)
  parser.add_argument('-u', '--user', help='The user to connect to carte as', default='cluster')
  parser.add_argument('-p', '--password', help='The password to connect to carte with', default='cluster')
  parser.add_argument('-q', '--port', help='The port to connect to carte at', default='8001')
  parser.add_argument('-d', '--destination', help='The top level destination directory', default='../app')
  args = parser.parse_args()
  def getData(relPath):
    request = urllib2.Request("http://localhost:%s/%s" % (args.port, relPath))
    auth = base64.encodestring('%s:%s' %(args.user, args.password)).replace('\n', '')
    request.add_header('Authorization', 'Basic %s' %auth)
    result = urllib2.urlopen(request)
    return result.read()

  def getListAndImages(listUrl, listFile, entryName):
    categories = json.loads(getData(listUrl))
    for category in categories:
      for entry in category[entryName]:
        print 'Getting image for ' + entry['name']
        data = getData(entry['image'])
        entryPath = 'img/' + entryName + '/' + entry['name'] + '.png'
        entry['image'] = entryPath
        with open(args.destination + '/' + entryPath, 'wb') as f:
          f.write(data)
    with open(args.destination + listFile, 'w') as f:
      f.write(json.dumps(categories))
  getListAndImages('kettle/kthin/stepList/', '/kettle/kthin/list/step', 'steps')
  getListAndImages('kettle/kthin/jobEntryList/', '/kettle/kthin/list/jobEntry', 'jobEntries')
