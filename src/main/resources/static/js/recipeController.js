sharpicApp.controller('recipeController', function($rootScope, $http, $location, $route, $scope, DTOptionsBuilder) {
    $scope.clientName = null;
    $scope.clientNames = [];

    $scope.recipes = [];
    $scope.recipesOptions = {};

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
        $scope.auditEntries = [];

        $scope.clientName = selectedClientName;

        $scope.recipesOptions = {
            data: [],
            enableSorting: false,
            enableColumnMenus: false,
            columnDefs: [
                {name: 'recipeName', displayName: 'Recipe Name', width : '30%',enableColumnMenus: false },
                {name: 'description', displayName: 'Recipe Items', enableColumnMenus: false},
                {name: 'action', displayName: '', width : '3%', enableColumnMenus: false, cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeRecipe(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
            ]
        };


        $scope.selectClient();
    };

    $scope.removeRecipe = function(row) {
        var index = $scope.recipesOptions.data.indexOf(row.entity);
        $scope.recipesOptions.data.splice(index, 1);
    };

    $scope.selectClient = function() {
        $http.get('/client/getClientRecipes?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.recipes = data;
            $scope.recipesOptions.data = data;
        })
        .error(function (data, status, header, config) {
        });
     };

});