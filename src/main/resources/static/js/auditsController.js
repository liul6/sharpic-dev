sharpicApp.controller('auditsController', function($rootScope, $http, $location, $route, $scope, $window, DTOptionsBuilder) {
    $scope.message ='this is a test for angularjs controller';
    $scope.clientName = null;
    $scope.auditDate = null;
    $scope.selectedVenue = null;

    $scope.clientNames = [];
    $scope.auditDates = [];
    $scope.allVenues = [];
    $scope.auditModiferItems = [];
    $scope.clientProducts = [];
    $scope.auditEntriesOptions = [];
    $scope.productDescriptionCelltemplate = null;

    $scope.clientLocations = [];
    $scope.clientLocationsMap  = {};

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withDisplayLength(100)
        .withOption('bLengthChange', false);

    $scope.getClientNames = function() {
        $http.get('/client/getClientNames')
            .success(function (data, status, headers, config) {
            $scope.clientNames = data;
            if($scope.clientNames.length>0) {
                $scope.populateDefault($scope.clientNames[0]);
            }
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getClientNames();

    $scope.populateDefault = function(selectedClientName) {
        $scope.clientName = selectedClientName;
        $scope.selectClient();
    };

    $scope.addEntry = function() {
        var newEntry = {productDescription : null, weights : null, fulls : null, bin : null, location : null};
        $scope.auditEntriesOptions.data.unshift(newEntry);
    };

    $scope.selectClient = function() {
        $scope.auditEntriesOptions.data = [];
        $scope.allVenus = [];
        $scope.clientProduct = [];

        $http.get('/client/getClientInfo?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.auditDates = data.model.clientAudits;
            $scope.allVenus = data.model.clientLocations;
            $scope.clientLocations = data.model.clientLocations;

            $scope.allVenus.push("ALL");
            $scope.clientProducts = data.model.clientProducts;
            $scope.productDescriptionCelltemplate = '<div><form name="inputForm"><input type="text" data-ng-model="MODEL_COL_FIELD" data-typeahead="description as clientProduct.description for clientProduct in grid.appScope.clientProducts | filter:$viewValue | limitTo:8" data-typeahead-on-select = "grid.appScope.typeaheadSelected(row.entity, $item)" class="form-control" ></form></div>';

            if($scope.auditDates.length>0) {
                $scope.auditDate = $scope.auditDates[0];
                $scope.selectAudit();
            }
            $scope.auditEntriesOptions = {
                data: [],
                enableRowSelection: false,
                enableCellEditOnFocus: true,
                multiSelect: false,
                enableColumnMenus: false,
                columnDefs: [
                    {name: 'clientProduct.description', displayName: 'Product', width : '40%', enableCellEdit : true, editableCellTemplate: $scope.productDescriptionCelltemplate },
                    {name: 'weights', displayName: 'Partials', type: 'number' },
                    {name: 'fulls', displayName: 'Fulls', type: 'number' },
                    {name: 'bin', displayName: 'Bin' },
                    {    name: 'location',
                         displayName: 'Location',
                         editableCellTemplate: 'ui-grid/dropdownEditor',
                         editDropdownIdLabel: 'locationName',
                         editDropdownValueLabel: 'locationName',
                         editDropdownOptionsArray: $scope.clientLocations},
                    {name: 'action', displayName: '', width : '4%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeEntry(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
                ]
            };

            $scope.auditEntriesOptions.onRegisterApi = function(gridApi){
                $scope.gridApi = gridApi;

                gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
                    $scope.$apply();
                });

                gridApi.rowEdit.on.saveRow($scope, $scope.saveEntryRow);
            };
        })
        .error(function (data, status, header, config) {
        });
    }

    $scope.saveEntryRow = function(rowEntity) {
         var promise = $scope.saveEntryToDatabase(rowEntity);
         $scope.gridApi.rowEdit.setSavePromise($scope.gridApi.grid, rowEntity, promise);
    }

    $scope.saveEntryToDatabase = function(rowEntity) {
        var data = angular.toJson(rowEntity.entity);
        return $q.defer();
        //$http.post('/audit/saveEntry', data);
    }

    $scope.typeaheadSelected = function(entity, selectedItem){
        entity.clientProduct = selectedItem;
        $scope.$broadcast('uiGridEventEndCellEdit');
    };

    $scope.locationTypeaheadSelected = function(entity, selectedItem){
        entity.location = selectedItem;
        $scope.$broadcast('uiGridEventEndCellEdit');
    };

    $scope.selectAudit = function() {
       $http.get('/audit/getEntries?auditDateStr=' + $scope.auditDate + '&clientName=' + $scope.clientName)
           .success(function (data, status, headers, config) {
           $scope.auditEntriesOptions.data = data;

           $http.get('/audit/getModifierItems?auditDateStr=' + $scope.auditDate + '&clientName=' + $scope.clientName)
               .success(function (data, status, headers, config) {
               $scope.auditModiferItems = data;
           })
               .error(function (data, status, header, config) {
           });

       })
       .error(function (data, status, header, config) {
       });


    };

    $scope.addAudit = function() {
        var data = $.param({
            clientName: $scope.clientName
        });

        var config = {
            headers : {
                'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
            }
        }

        $http.post('/client/addAudit', data, config)
            .success(function (data, status, headers, config) {
                if(data != null) {
                    $scope.auditEntriesOptions.data = [];
                    $scope.auditDate = data;
                    $scope.selectClient();
                }
        })
        .error(function (data, status, header, config) {
        //notify here
        });
    };

    $scope.removeEntry = function(row) {
        var data = angular.toJson(row.entity);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }

        $http.post('/audit/deleteEntry', data)
            .success(function (data, status, headers, config) {
                if(data != null && data.successful) {
                    var index = $scope.auditEntriesOptions.data.indexOf(row.entity);
                    $scope.auditEntriesOptions.data.splice(index, 1);
                }
        })
        .error(function (data, status, header, config) {
        });
    };

    $scope.removeAudit = function() {
        var data = $.param({
            clientName: $scope.clientName,
            auditDateStr: $scope.auditDate
        });

        var config = {
            headers : {
                'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
            }
        }

        $http.post('/client/deleteAudit', data, config)
            .success(function (data, status, headers, config) {
            if(data != null) {
                $scope.populateDefault($scope.clientName);
            }
        })
        .error(function (data, status, header, config) {
        //notify here
        });
    };
})
.filter('locationFilter', function() {
  var genderHash = $scope.clientLocationsMap;

  return function(input) {
    if (!input){
      return '';
    } else {
      return genderHash[input];
    }
  };
});