sharpicApp.controller('auditsController', function($rootScope, $http, $location, $route, $scope) {
    $scope.message ='this is a test for angularjs controller';
    $scope.clientName = null;
    $scope.auditDate = null;
    $scope.selectedVenue = null;

    $scope.clientNames = [];
    $scope.auditDates = [];
    $scope.allVenues = [];
    $scope.auditEntries = [];

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
        $scope.selectClient();
    };

    $scope.selectClient = function() {
        $scope.auditEntries = [];
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
                    $scope.selectClient();
                    $scope.auditDate = data;
                }
        })
        .error(function (data, status, header, config) {
        //notify here
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
});