sharpicApp.controller('productController', function($rootScope, $http, $location, $route, $scope, $window, DTOptionsBuilder) {
    $scope.clientName = null;

    $scope.clientNames = [];
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
        $scope.selectClient();
    };

    $scope.getClientNames();

    $scope.getProducts = function() {
        $http.get('/product/getProducts')
            .success(function (data, status, headers, config) {
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
                        {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeProduct(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
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
                $scope.sizeOptions = {
                    data: data,
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
        $http.get('/product/getClientProducts?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {

            $scope.productCelltemplate = '<div><form name="inputForm"><input type="text" data-ng-model="MODEL_COL_FIELD" data-typeahead="name as product.name for product in grid.appScope.productOptions.data | filter:$viewValue | limitTo:8" data-typeahead-on-select = "grid.appScope.typeaheadSelectedProduct(row.entity, $item)" class="form-control" ></form></div>';
            $scope.sizeCelltemplate = '<div><form name="inputForm"><input type="text" data-ng-model="MODEL_COL_FIELD" data-typeahead="name as size.name for size in grid.appScope.sizeOptions.data | filter:$viewValue | limitTo:8" data-typeahead-on-select = "grid.appScope.typeaheadSelectedSize(row.entity, $item)" class="form-control" ></form></div>';

            $scope.clientProductOptions = {
                data: data,
                enableRowSelection: false,
                enableCellEditOnFocus: true,
                multiSelect: false,
                enableColumnMenus: false,
                columnDefs: [
                    {name: 'name', displayName: 'Product', width : '40%', enableCellEdit : true, editableCellTemplate: $scope.productCelltemplate },
                    {name: 'size.name', displayName: 'Size', enableCellEdit : true, editableCellTemplate: $scope.sizeCelltemplate },
                    {name: 'serving', displayName: 'Serving' },
                    {name: 'retailPrice', displayName: 'Retail Price', type: 'number' },
                    {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeClientProduct(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
                ]
            };

            $scope.clientProductOptions.onRegisterApi = function(gridApi){
                $scope.gridApi = gridApi;
                gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
                $scope.$apply();
                });
            };

        })
        .error(function (data, status, header, config) {
        });
    };


    $scope.typeaheadSelectedProduct = function(entity, selectedItem){
        entity.name = selectedItem.name;
        $scope.$broadcast('uiGridEventEndCellEdit');
    };

    $scope.typeaheadSelectedSize = function(entity, selectedItem){
        entity.size = selectedItem;
        $scope.$broadcast('uiGridEventEndCellEdit');
    };

    $scope.addProduct = function() {
        var newProduct = {name : null, fulls : null, costs : null, tare : null, cases : null, upc : null, tags : null};
        $scope.productOptions.data.unshift(newProduct);
    };

    $scope.removeProduct = function(row) {
        var data = angular.toJson(row.entity);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }

        $http.post('/product/deleteProduct', data)
            .success(function (data, status, headers, config) {
                if(data != null) {
                    if(data.successful) {
                        var index = $scope.productOptions.data.indexOf(row.entity);
                        $scope.productOptions.data.splice(index, 1);
                    }
                    else {
                        $window.alert(data.errorText);
                    }
                }
        })
        .error(function (data, status, header, config) {
        });
    };

    $scope.addClientProduct = function() {
        var newClientProduct = {name : null, size : {name : null}, serving : null, retailPrice : null};
        $scope.clientProductOptions.data.unshift(newClientProduct);
    };

    $scope.removeClientProduct = function(row) {
        var index = $scope.clientProductOptions.data.indexOf(row.entity);
        $scope.clientProductOptions.data.splice(index, 1);
    };

    $scope.addSize = function() {
        var newSize = {name : null, ounces : null};
        $scope.sizeOptions.data.unshift(newSize);
    };

    $scope.removeSize = function(row) {
        var index = $scope.sizeOptions.data.indexOf(row.entity);
        $scope.sizeOptions.data.splice(index, 1);
    };
});