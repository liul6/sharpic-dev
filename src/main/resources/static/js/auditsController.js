sharpicApp.controller('auditsController', function($rootScope, $http, $location, $route, $scope, $window, Notify, $interval, DTOptionsBuilder) {
    $scope.clientName = null;
    $scope.auditDate = null;
    $scope.selectedVenue = null;

    $scope.clientNames = [];
    $scope.auditDates = [];
    $scope.allVenues = [];
    $scope.auditModiferItems = [];
    $scope.products = [];
    $scope.auditEntriesOptions = [];
    $scope.auditModifierItemsOptions = [];
    $scope.productDescriptionCelltemplate = null;

    $scope.clientLocations = [];
    $scope.clientLocationsMap  = {};

    $scope.audit = null;

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
        var newEntry = {productDescription : null, weights : null, fulls : null, bin : null, location : null, product : null, productId : null};
        $scope.auditEntriesOptions.data.unshift(newEntry);
    };

    $scope.selectClient = function() {
        $scope.auditEntriesOptions.data = [];
        $scope.allVenus = [];

        $http.get('/client/getClientInfo?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            if(data.successful) {
                $scope.auditDates = data.model.clientAudits;
                $scope.allVenus = data.model.clientLocations;
                $scope.clientLocations = data.model.clientLocations;

                $scope.allVenus.push("ALL");
                $scope.products = data.model.products;

                $scope.productDescriptionCelltemplate = '<div><form name="inputForm"><input type="text" data-ng-model="MODEL_COL_FIELD" data-typeahead="description as product.description for product in grid.appScope.products | filter:$viewValue | limitTo:8" data-typeahead-on-select = "grid.appScope.typeaheadSelected(row.entity, $item)" class="form-control" ></form></div>';

                $scope.configAuditEntryGrid();
                if($scope.auditDates.length>0) {
                    $scope.auditDate = $scope.auditDates[0];
                    $scope.selectAudit();
                }
            }
            else {
                Notify.addMessage(data.errorText, 'danger');
            }
        })
        .error(function (data, status, header, config) {
            Notify.addMessage(data.errorText, 'danger');
        });
    }

    $scope.auditEntriesOptions.onRegisterApi = function(gridApi){
        $scope.gridApi = gridApi;

        gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
            if(newValue != oldValue) {
                $scope.saveEntry(rowEntity);
            }
        });
    };

    $scope.configAuditEntryGrid = function() {
        $scope.auditEntriesOptions = {
            data: [],
            infiniteScrollRowsFromEnd: 40,
            infiniteScrollUp: true,
            infiniteScrollDown: true,
            enableRowSelection: false,
            enableCellEditOnFocus: true,
            multiSelect: false,
            enableColumnMenus: false,
            columnDefs: [
                {name: 'product.description', displayName: 'Product', width : '40%', enableCellEdit : true, editableCellTemplate: $scope.productDescriptionCelltemplate },
                {name: 'weights', displayName: 'Partials', type: 'number' },
                {name: 'amount', displayName: 'Fulls', type: 'number' },
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
    }

    $scope.configAuditModifierGrid = function() {

        $scope.auditModifierItemsOptions = {
            data: [],
            enableRowSelection: false,
            enableCellEditOnFocus: true,
            multiSelect: false,
            enableColumnMenus: false,
            columnDefs: [
                {name: 'product.description', displayName: 'Product', width : '40%', enableCellEdit : true, editableCellTemplate: $scope.productDescriptionCelltemplate },
                {name: 'weights', displayName: 'Partials', type: 'number' },
                {name: 'amount', displayName: 'Fulls', type: 'number' },
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
    }

    $scope.saveEntry = function(entry) {
        if( entry.product == null && entry.productId == null)
            return;

        if(entry.weights==null && entry.amount==null)
            return;

        if(entry.auditId == null)
            entry.auditId = $scope.audit.id;

        var data = angular.toJson(entry);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }
        var index = $scope.auditEntriesOptions.data.indexOf(entry);

        $http.post('/audit/saveEntry', data)
            .success(function (data, status, headers, config) {
            if(data.successful) {
                Notify.addMessage('Entry saved successfully', 'success');
                entry = data.model.entry;
                $scope.auditEntriesOptions.data[index] = entry;
            }
            else {
                Notify.addMessage('Entry failed to save: ' + data.errorText, 'danger');
            }
        })
        .error(function (data, status, header, config) {
            Notify.addMessage('Entry failed to save: ' + data.errorText, 'danger');
        });
    }

    $scope.typeaheadSelected = function(entity, selectedItem){
        entity.product = selectedItem;
        $scope.$broadcast('uiGridEventEndCellEdit');
    };

    $scope.locationTypeaheadSelected = function(entity, selectedItem){
        entity.location = selectedItem;
        $scope.$broadcast('uiGridEventEndCellEdit');
    };

    $scope.selectAudit = function() {
        $http.get('/audit/getAuditInfo?auditDateStr=' + $scope.auditDate + '&clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            if(data.successful)
            {
                $scope.auditEntriesOptions.data = data.model.entries;
                $scope.auditModiferItems = data.model.modifierItems;
                $scope.audit = data.model.audit;
            }
            else {
                Notify.addMessage(data.errorText, 'danger');
            }
       })
       .error(function (data, status, header, config) {
            Notify.addMessage(data.errorText, 'danger');
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
                if(data != null && data.successful) {
                    $scope.auditEntriesOptions.data = [];
                    $scope.auditDate = data.model.addedAuditDate;
                    $scope.selectClient();
                    Notify.addMessage('Audit added successfully', 'success');
                }
                else {
                    Notify.addMessage('Audit failed to add: ' + data.errorText, 'danger');
                }
        })
        .error(function (data, status, header, config) {
            Notify.addMessage('Audit failed to add: ' + data.errorText, 'danger');
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
                    Notify.addMessage('Entry deleted successfully', 'success');

                    var index = $scope.auditEntriesOptions.data.indexOf(row.entity);
                    $scope.auditEntriesOptions.data.splice(index, 1);
                }
                else {
                    Notify.addMessage('Entry failed to delete: ' + data.errorText, 'danger');
                }
        })
        .error(function (data, status, header, config) {
            Notify.addMessage('Entry failed to delete: ' + data.errorText, 'danger');
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
            if(data != null && data.successful) {
                $scope.populateDefault($scope.clientName);
                Notify.addMessage('Audit deleted successfully', 'success');
            }
            else {
                Notify.addMessage('Audit failed to delete: ' + data.errorText, 'danger');
            }
        })
        .error(function (data, status, header, config) {
            Notify.addMessage('Audit failed to delete: ' + data.errorText, 'danger');
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