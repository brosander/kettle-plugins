'use strict';

/* App Module */

var kettleApp = angular.module('kettleApp', [
  'ngRoute',
  'kettleServices',
  'kettleControllers'
]);

// Directive from Leon Radley's answer on Stack Overflow:
// http://stackoverflow.com/questions/15895483/angular-ng-href-and-svg-xlink
kettleApp.directive('ngXlinkHref', function () {
  return {
    priority: 99,
    restrict: 'A',
    link: function (scope, element, attr) {
      var attrName = 'xlink:href';
      attr.$observe('ngXlinkHref', function (value) {
        if (!value)
          return;

        attr.$set(attrName, value);
      });
    }
  };
});

kettleApp.directive("kthinDialog", [ '$interpolate', function($interpolate) {
  return function(scope, element, attrs) {
    var title = element.attr("title");
    if (!title) {
      title = "";
    }
    var titleInterp = $interpolate(title);
    var makeDialog = function() {
      var tryClose = function(click) {
        // Take jquery ui off dialog div
        element.dialog( "destroy" );
        // Perform click event, add dialog back if result is suppress
        if (scope.$eval(element.attr(click)) == "suppress") {
          makeDialog();
        }
      };
      var dialog = $(element).dialog({
        width: 'auto',
        draggable: true,
        closeOnEscape: false,
        title: scope.$eval(titleInterp),
        buttons: [
          { text: "Ok", click: function(e) {
            tryClose("okonclick");
          }},
          { text: "Cancel", click: function(e) {
            tryClose("cancelonclick");
          }}
        ]
      });
    };
    makeDialog();
  };
}]);

kettleApp.directive("kthinMenu", function() {
  return function(scope, element, attrs) {
    $(element).menu({position: {my: "bottom"}});
  };
});

kettleApp.directive("kthinDraggable", function() {
  return function(scope, element, attrs) {
    $(element).draggable({
      revert: "invalid",
      containment: "document",
      helper: "clone",
      cursor: "move"
    });
  };
});

kettleApp.directive("kthinDroppable", function() {
  return function(scope, element, attrs) {
    var dropHelper = element.attr("dropHelper");
    if (!dropHelper) {
      dropHelper = "ondrop";
    }
    $(element).droppable({
      accept: ".stepType",
      // activeClasscontainment: "document",
      drop: function( event, ui) {
        var dragElement = angular.element(ui.draggable);
        var kthinVar = dragElement.attr("kthin-var");
        if (!kthinVar) {
          kthinVar = "element";
        }
        var elementPosition = $(element).offset()
        scope[dropHelper](dragElement.scope()[kthinVar], event.pageX - elementPosition.left, event.pageY - elementPosition.top);
      }
    });
  };
});

kettleApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/kettle', {
        templateUrl: 'partials/kettle-view.html',
        controller: 'KettleController'
      }).
      otherwise({
        redirectTo: '/kettle'
      });
  }]);
