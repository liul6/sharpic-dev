sharpicApp.controller('auditsController', function($rootScope, $http, $location, $route, $scope) {
    $scope.message ='this is a test for angularjs controller';
    $scope.clientName = null;
    $scope.auditDate = null;

    $scope.clientNames = ["Emil", "Tobias", "Linus"];
    $scope.auditDates = ["2016-11-01", "2016-11-08", "2016-11-15"];

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

});