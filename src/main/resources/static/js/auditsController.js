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
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getClientNames();

    $scope.selectClient = function() {
        $http.get('/client/getAuditDates?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.auditDates = data;
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

    $scope.refreshEntryPage = function() {
       $http.get('/audit/getEntries?auditDateStr=' + $scope.auditDate)
           .success(function (data, status, headers, config) {
           $scope.auditEntries = data;
       })
       .error(function (data, status, header, config) {
       });
    }

});