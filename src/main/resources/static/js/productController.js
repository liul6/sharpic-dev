sharpicApp.controller('productController', function($rootScope, $http, $location, $route, $scope, DTOptionsBuilder) {
    $scope.clientName = null;

    $scope.clientNames = [];
    $scope.products = [];
    $scope.clientProducts = [];
    $scope.sizes = [];
    $scope.productOptions = {};
    $scope.clientProductOptions = {};
    $scope.sizeOptions = {};

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

    $scope.populateDefault = function(selectedClientName) {
        $scope.clientName = selectedClientName;
        $scope.clientProductOptions = {
            data: [],
            enableSorting: false,
            enableColumnMenus: false,
            columnDefs: [
                {name: 'name', displayName: 'Product', width : '40%' },
                {name: 'size.name', displayName: 'Size' },
                {name: 'serving', displayName: 'Serving' },
                {name: 'retailPrice', displayName: 'Retail Price', type: 'number' },
                {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeProduct(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
            ]
        };

        $scope.selectClient();
    };

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withDisplayLength(100)
        .withOption('bLengthChange', false);

    $scope.getClientNames();

    $scope.getProducts = function() {
        $http.get('/product/getProducts')
            .success(function (data, status, headers, config) {
                $scope.products = data;
                $scope.productOptions = {
                    data: data,
                    enableSorting: false,
                    enableColumnMenus: false,
                    columnDefs: [
                        {name: 'name', displayName: 'Product Name', width : '40%' },
                        {name: 'fulls', displayName: 'Fulls', type: 'number' },
                        {name: 'costs', displayName: 'Costs', type: 'number' },
                        {name: 'tare', displayName: 'Tare', type: 'number' },
                        {name: 'cases', displayName: 'Cases', type: 'number' },
                        {name: 'upc', displayName: 'UPC', width : '12%'  },
                        {name: 'tags', displayName: 'Tags', width : '12%' },
                        {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeClientProduct(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
                    ]
                };
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getProducts();

    $scope.getSizes = function() {
        $http.get('/product/getSizes')
            .success(function (data, status, headers, config) {
                $scope.sizes = data;
                $scope.sizeOptions = {
                    data: data,
                    enableSorting: false,
                    enableColumnMenus: false,
                    columnDefs: [
                        {name: 'name', displayName: 'Name', width : '40%' },
                        {name: 'ounces', displayName: 'Ounces', type: 'number' },
                        {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeSize(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
                    ]
                };
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getSizes();

    $scope.selectClient = function() {
        $scope.clientProducts = [];
        $http.get('/product/getClientProducts?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.clientProducts = data;
            $scope.clientProductOptions.data = data;
        })
        .error(function (data, status, header, config) {
        });
    };

    $scope.addProduct = function() {
        var newProduct = {name : null, fulls : null, costs : null, tare : null, cases : null, upc : null, tags : null};
        $scope.productOptions.data.unshift(newProduct);
    };

    $scope.removeProduct = function(row) {
        var index = $scope.productOptions.data.indexOf(row.entity);
        $scope.productOptions.data.splice(index, 1);
    };


    $scope.removeClientProduct = function(row) {
        var index = $scope.clientProductOptions.data.indexOf(row.entity);
        $scope.clientProductOptions.data.splice(index, 1);
    };


    $scope.removeSize = function(row) {
        var index = $scope.sizeOptions.data.indexOf(row.entity);
        $scope.sizeOptions.data.splice(index, 1);
    };
});