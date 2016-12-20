sharpicApp.controller('recipeController', function($rootScope, $http, $location, $route, $scope, DTOptionsBuilder) {
    $scope.clientName = null;
    $scope.clientNames = [];

    $scope.recipes = [];

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withDisplayLength(50)
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
        $scope.auditEntries = [];

        $scope.clientName = selectedClientName;
        $scope.selectClient();
    };

    $scope.selectClient = function() {
        $http.get('/client/getClientRecipes?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.recipes = data;
        })
        .error(function (data, status, header, config) {
        });
     };

});