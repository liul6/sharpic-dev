sharpicApp.controller('productController', function($rootScope, $http, $location, $route, $scope, DTOptionsBuilder) {
    $scope.clientName = null;

    $scope.clientNames = [];
    $scope.products = [];
    $scope.sizes = [];

    $scope.getClientNames = function() {
        $http.get('/client/getClientNames')
            .success(function (data, status, headers, config) {
            $scope.clientNames = data;
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withDisplayLength(25)
        .withOption('bLengthChange', false);

    $scope.getClientNames();

    $scope.getProducts = function() {
        $http.get('/product/getProducts')
            .success(function (data, status, headers, config) {
                $scope.products = data;
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getProducts();

    $scope.getSizes = function() {
        $http.get('/product/getSizes')
            .success(function (data, status, headers, config) {
                $scope.sizes = data;
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getSizes();
});