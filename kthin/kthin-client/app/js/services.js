'use strict';

/* Services */

var kettleServices = angular.module('kettleServices', ['ngResource']);

kettleServices.factory('Transformation', ['$resource',
  function($resource){
    return $resource('transformations/:transName.json', {}, {
      query: {method:'GET', params:{transName:'transName'}}
    });
  }]);

kettleServices.factory('Step', ['$resource',
  function($resource){
    return $resource('steps/:stepName.json', {}, {
      query: {method:'GET', params:{stepName:'stepName'}}
    });
  }]);

kettleServices.factory('EntryList', ['$resource',
  function($resource){
    return $resource('kettle/kthin/list/:entryType', {}, {
      query: {method:'GET', params:{entryType : 'entryType'}, isArray: true}
    });
  }]);
