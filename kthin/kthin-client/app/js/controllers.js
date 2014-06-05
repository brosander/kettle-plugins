'use strict';

/* Controllers */

var kettleControllers = angular.module('kettleControllers', []);

kettleControllers.controller('KettleController', ['$scope', 'Transformation', 'Step', 'StepList', 'JobEntryList',
  function($scope, Transformation, Step, StepList, JobEntryList) {
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
    $scope.jobEntryList = JobEntryList.query();
    $scope.editors = [];
    $scope.editingEntries = {};
    $scope.zoomPercent = "100%";

    $scope.openTransformation = function(transName) {
      var transReturn = ({"name": transName, "_type": "transformation"});
      $scope.editors.push(transReturn);
      $scope.activeEditor = transReturn;
      Transformation.get({transName: transName}, function(trans) {
        var entries = {};
        trans["name"] = transName;
        trans["_type"] = "transformation";
        angular.copy(trans, transReturn);
        angular.forEach(transReturn.entries, function(entry) {
          entry.width = 32;
          entry.height = 32;
          entry.editing = false;
          entry.kthinId = $scope.sequence++;
          entries[entry.name] = entry;
          Step.get({stepName: entry.type}, function(stepDef) {
            entry.definition= stepDef;
            entry._defined = true;
          });
        });

        angular.forEach(transReturn.hops, function(hop) {
          hop.from = entries[hop.from];
          hop.to = entries[hop.to];
          hop._defined = true;
        });
      });
    }

    $scope.setActiveEditor = function(editor) {
      $scope.activeEditor = editor;
    }

    $scope.entryOnMouseDown = function(e, entry) {
      var rect = angular.element(e.target);
      var svg = $(rect.parent());
      svg.css( 'cursor', 'move' );
      entry.xOffset = e.pageX - entry.location.x;
      entry.yOffset = e.pageY - entry.location.y;
      svg.mousemove(function(moveEvent) {
        entry.location.x = moveEvent.pageX - entry.xOffset;
        entry.location.y = moveEvent.pageY - entry.yOffset;
        rect.scope().$apply();
      });
      var mouseUp = function() {
        svg.css( 'cursor', 'auto' );
        svg.unbind("mousemove");
        svg.unbind("mouseup");
      };
      svg.mouseup(mouseUp);
    };

    $scope.entryOnDoubleClick = function(e, entry) {
      $scope.editingEntries[entry.kthinId] = { entry: entry, entryCopy: angular.copy(entry)};
      entry.editing = true;
    };

    $scope.getMiddleX = function(entry) {
      return entry.location.x + entry.width / 2;
    };

    $scope.getMiddleY = function(entry) {
      return entry.location.y + entry.height / 2;
    };

    $scope.getMiddle = function(entry) {
      return { "x": getMiddleX(entry), "y": getMiddleY(entry) };
    };

    $scope.save = function(entry) {
      angular.copy($scope.editingEntries[entry.kthinId].entryCopy, $scope.editingEntries[entry.kthinId].entry);
      $scope.cancel(entry);
    };

    $scope.cancel = function(entry) {
      entry.editing = false;
      delete $scope.editingEntries[entry.kthinId];
      $scope.$apply();
    }

    $scope.onEntryTypeDrop = function(entryType, offsetX, offsetY) {
      var entry = {
        "name": entryType.label,
        "type": entryType.type,
        "kthinId": $scope.sequence++,
        "location": {"x": offsetX, "y":offsetY},
        "width": 32,
        "height": 32,
        "definition": { "image": entryType.image },
        "_defined": true
        };
      $scope.activeEditor.entries.push(entry);
      Step.get({stepName: entryType.name}, function(stepDef) {
        entry.definition= stepDef;
      });
      $scope.$apply();
    };

    $scope.expandAllCategories = function(expanded) {
      angular.forEach($scope.stepList, function(category){category.expanded = expanded;});
    };
  }]);
