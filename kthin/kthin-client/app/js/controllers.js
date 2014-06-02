'use strict';

/* Controllers */

var kettleControllers = angular.module('kettleControllers', []);

kettleControllers.controller('KettleController', ['$scope', 'Transformation', 'Step', 'StepList',
  function($scope, Transformation, Step, StepList) {
    $scope.fileOpen = function() {
      $scope.openTransformation("test1")
    };

    $scope.menus = [
      {
        "name": "File",
        "submenus": [
          {
            "name": "New",
            "submenus": [
              {
                "name": "Job"
              },
              {
                "name": "Transformation"
              }
            ]
          },
          {
            "name": "Open",
            "action": $scope.fileOpen
          }
        ]
      },
      {
        "name": "Edit"
      }
    ];

    angular.forEach($scope.menus, function(menu) {
      menu.top = true;
    });

    $scope.collapseMenu = function(menu) {
      if(menu.submenus) {
        menu.expanded = false;
        angular.forEach(menu.submenus, function(submenu){$scope.collapseMenu(submenu)});
      }
    }

    $scope.menuClick = function(menu, event) {
      event.stopPropagation();
      if (menu.action) {
        menu.action();
        angular.forEach($scope.menus, function(menu) {
          $scope.collapseMenu(menu);
        });
      } else if (menu.submenus) {
        if (menu.expanded) {
          $scope.collapseMenu(menu);
        } else {
          menu.expanded = true;
        }
      }
    };

    $scope.documentClick = function(e) {
      if (!$(e.target).closest("#toolbar").length) {
        angular.forEach($scope.menus, function(menu) {$scope.collapseMenu(menu)});
      }
      $scope.$apply();
    };

    $(document).click($scope.documentClick);

    $scope.sequence = 0;
    $scope.stepList = StepList.query();
    $scope.editors = [];
    $scope.editingEntries = {};
    $scope.zoomPercent = "100%";

    $scope.openTransformation = function(transName) {
      var transReturn = ({"name": transName, "_type": "transformation"});
      $scope.editors.push(transReturn);
      $scope.activeEditor = transReturn;
      Transformation.get({transName: transName}, function(trans) {
        var steps = {};
        trans["name"] = transName;
        trans["_type"] = "transformation";
        angular.copy(trans, transReturn);
        angular.forEach(transReturn.steps, function(step) {
          step.width = 32;
          step.height = 32;
          step.editing = false;
          step.kthinId = $scope.sequence++;
          steps[step.name] = step;
          Step.get({stepName: step.type}, function(stepDef) {
            step.definition= stepDef;
            step._defined = true;
          });
        });

        angular.forEach(transReturn.hops, function(hop) {
          hop.from = steps[hop.from];
          hop.to = steps[hop.to];
          hop._defined = true;
        });
      });
    }

    $scope.setActiveEditor = function(editor) {
      $scope.activeEditor = editor;
    }

    $scope.stepOnMouseDown = function(e, step) {
      var rect = angular.element(e.target);
      var svg = $(rect.parent());
      svg.css( 'cursor', 'move' );
      step.xOffset = e.pageX - step.location.x;
      step.yOffset = e.pageY - step.location.y;
      svg.mousemove(function(moveEvent) {
        step.location.x = moveEvent.pageX - step.xOffset;
        step.location.y = moveEvent.pageY - step.yOffset;
        rect.scope().$apply();
      });
      var mouseUp = function() {
        svg.css( 'cursor', 'auto' );
        svg.unbind("mousemove");
        svg.unbind("mouseup");
      };
      svg.mouseup(mouseUp);
    };

    $scope.stepOnDoubleClick = function(e, step) {
      $scope.editingEntries[step.kthinId] = { step: step, stepCopy: angular.copy(step)};
      step.editing = true;
    };

    $scope.getMiddleX = function(step) {
      return step.location.x + step.width / 2;
    };

    $scope.getMiddleY = function(step) {
      return step.location.y + step.height / 2;
    };

    $scope.getMiddle = function(step) {
      return { "x": getMiddleX(step), "y": getMiddleY(step) };
    };

    $scope.save = function(step) {
      angular.copy($scope.editingEntries[step.kthinId].stepCopy, $scope.editingEntries[step.kthinId].step);
      $scope.cancel(step);
    };

    $scope.cancel = function(step) {
      step.editing = false;
      delete $scope.editingEntries[step.kthinId];
      $scope.$apply();
    }

    $scope.onStepTypeDrop = function(stepType, offsetX, offsetY) {
      var step = {
        "name": stepType.label,
        "type": stepType.type,
        "kthinId": $scope.sequence++,
        "location": {"x": offsetX, "y":offsetY},
        "width": 32,
        "height": 32,
        "definition": { "image": stepType.image },
        "_defined": true
        };
      $scope.activeEditor.steps.push(step);
      Step.get({stepName: stepType.name}, function(stepDef) {
        step.definition= stepDef;
      });
      $scope.$apply();
    };

    $scope.expandAllCategories = function(expanded) {
      angular.forEach($scope.stepList, function(category){category.expanded = expanded;});
    };
  }]);
