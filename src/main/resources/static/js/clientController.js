sharpicApp.controller('clientController', function($rootScope, $http, $location, $route, $scope) {
    $scope.clients = null;

    $scope.getClients = function() {
        $http.get('/client/getClients')
            .success(function (data, status, headers, config) {
            $scope.clients = data;
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getClients();
});