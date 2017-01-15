sharpicApp.controller('recipeController', function($rootScope, $http, $location, $route, $scope, $modal) {
    $scope.clientName = null;
    $scope.clientNames = [];

    $scope.recipesOptions = {};

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
        $scope.clientName = selectedClientName;
        var descriptionCelltemplate = '<div ng-click="grid.appScope.modifyRecipe(row.entity)"><a href="">{{row.entity.description}}</a></div>';
        // '<div><a href ng-click="modifyRecipe()">{{row.entity.description}}</a></div>'

        $scope.recipesOptions = {
            data: [],
            enableColumnMenus: false,
            columnDefs: [
                {name: 'recipeName', displayName: 'Recipe Name', width : '30%', enableCellEdit : false  },
                {name: 'description', displayName: 'Recipe Items', enableCellEdit : false, cellTemplate : descriptionCelltemplate },
                {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeRecipe(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
            ]
        };

        $scope.selectClient();
    };

    $scope.addRecipe = function() {
        var newRecipe = {recipeName : null, description : null};
        $scope.recipesOptions.data.unshift(newRecipe);
    };

    $scope.removeRecipe = function(row) {
        var index = $scope.recipesOptions.data.indexOf(row.entity);
        $scope.recipesOptions.data.splice(index, 1);
    };

    $scope.modifyRecipe = function(entity) {
        $scope.opts = {
            dialogFade: false,
            keyboard: true,
            templateUrl : 'pages/modifyRecipe.html',
            controller : modifyRecipeController,
            resolve: {

            } // empty storage
        };

        $scope.opts.resolve.recipe = function() {
            return angular.copy(entity); // {recipeName : entity.recipeName} pass name to Dialog
        }

        var modalInstance = $modal.open($scope.opts);

        modalInstance.result.then(function(){
            //on ok button press
        },function(){
            //on cancel button press
            console.log("Modal Closed");
        });
    };

    $scope.selectClient = function() {
        $http.get('/client/getClientRecipes?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.recipesOptions.data = data;
        })
        .error(function (data, status, header, config) {
        });
    };


})

var modifyRecipeController = function($scope, $modalInstance, $modal, recipe) {
     $scope.recipe = recipe;
     $scope.recipeItemOptions = {
        data: recipe.recipeItems,
        enableSorting: false,
        enableColumnMenus: false,
        columnDefs: [
            {name: 'clientProduct.name', displayName: 'Product Name', width : '40%' },
            {name: 'fulls', displayName: 'Fulls', type: 'number' },
            {name: 'ounces', displayName: 'Ounces', type: 'number' }
//            {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeProduct(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
        ]

     };

      $scope.ok = function () {
        $modalInstance.close();
      };

      $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
      };
};
