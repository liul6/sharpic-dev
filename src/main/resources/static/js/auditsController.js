sharpicApp.controller('auditsController', function($rootScope, $http, $location, $route, $scope, DTOptionsBuilder) {
    $scope.message ='this is a test for angularjs controller';
    $scope.clientName = null;
    $scope.auditDate = null;
    $scope.selectedVenue = null;

    $scope.clientNames = [];
    $scope.auditDates = [];
    $scope.allVenues = [];
    $scope.auditEntries = [];
    $scope.auditModiferItems = [];
    $scope.auditEntriesOptions = [];

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withDisplayLength(100)
        .withOption('bLengthChange', false);

    $scope.addAudit = function() {
        $scope.message = 'Added audit succcessfully';
    };

    $scope.deleteAudit = function() {
        $scope.message = 'Deleted audit succcessfully';
    };

    $scope.addEntry = function() {
        $scope.message = 'Added entry succcessfully';
    };

    $scope.addModifiers = function() {
        $scope.message = 'Added modifiers succcessfully';
    };

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
        $scope.auditEntries = [];

        $scope.clientName = selectedClientName;

        $scope.auditEntriesOptions = {
            data: [],
            enableSorting: false,
            enableColumnMenus: false,
            columnDefs: [
                {name: 'productDescription', displayName: 'Product', width : '40%', enableSorting: false },
                {name: 'weights', displayName: 'Partials', type: 'number', enableSorting: false },
                {name: 'fulls', displayName: 'Fulls', type: 'number', enableSorting: false },
                {name: 'bin', displayName: 'Bin', enableSorting: false },
                {name: 'location', displayName: 'Location', enableSorting: false },
                {name: 'action', displayName: '', width : '3%', enableSorting: false, cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeEntry(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
            ]
        };
        $scope.selectClient();
    };

    $scope.selectClient = function() {
        $scope.auditEntries = [];
        $scope.auditEntriesOptions.data = [];
        $http.get('/client/getAuditDates?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.auditDates = data;
            if($scope.auditDates.length>0) {
                $scope.auditDate = $scope.auditDates[0];
                $scope.selectAudit();
            }
        })
        .error(function (data, status, header, config) {
        });

        $http.get('/client/getLocations?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.allVenus = [];
            $scope.allVenus = data;
            $scope.allVenus.push("ALL");
        })
        .error(function (data, status, header, config) {
        });
    }

    $scope.selectAudit = function() {
       $http.get('/audit/getEntries?auditDateStr=' + $scope.auditDate + '&clientName=' + $scope.clientName)
           .success(function (data, status, headers, config) {
           $scope.auditEntries = data;
           $scope.auditEntriesOptions.data = data;
       })
       .error(function (data, status, header, config) {
       });

       $http.get('/audit/getModifierItems?auditDateStr=' + $scope.auditDate + '&clientName=' + $scope.clientName)
           .success(function (data, status, headers, config) {
           $scope.auditModiferItems = data;
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
                    $scope.auditEntries = [];
                    $scope.auditEntriesOptions.data = [];
                    $scope.selectClient();
                    $scope.auditDate = data;
                }
        })
        .error(function (data, status, header, config) {
        //notify here
        });
    };

    $scope.removeEntry = function(row) {
        var index = $scope.auditEntriesOptions.data.indexOf(row.entity);
        $scope.auditEntriesOptions.data.splice(index, 1);
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
});