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

kettleServices.factory('StepList', ['$resource',
  function($resource){
    return $resource('kettle/kthin/stepList', {}, {
      query: {method:'GET', isArray: true}
    });
  }]);

kettleServices.factory('JobEntryList', ['$resource',
  function($resource){
    return $resource('kettle/kthin/jobEntryList', {}, {
      query: {method:'GET', isArray: true}
    });
  }]);
